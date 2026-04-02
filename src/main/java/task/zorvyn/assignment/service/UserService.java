package task.zorvyn.assignment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import task.zorvyn.assignment.entity.Role;
import task.zorvyn.assignment.entity.User;
import task.zorvyn.assignment.entity.UserStatus;
import task.zorvyn.assignment.exception.ResourceNotFoundException;
import task.zorvyn.assignment.repository.UserRepository;

import java.util.List;

/**
 * User business logic.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User payload is required");
        }

        String username = user.getUsername() == null ? null : user.getUsername().trim();
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole() == null ? Role.VIEWER : user.getRole());
        user.setStatus(user.getStatus() == null ? UserStatus.ACTIVE : user.getStatus());

        return userRepository.save(user);
    }

    @Transactional
    public User assignRole(Long userId, Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }

        User user = getUserById(userId);
        user.setRole(role);
        return userRepository.save(user);
    }

    @Transactional
    public User changeStatus(Long userId, UserStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }

        User user = getUserById(userId);
        user.setStatus(status);
        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));
    }
}
