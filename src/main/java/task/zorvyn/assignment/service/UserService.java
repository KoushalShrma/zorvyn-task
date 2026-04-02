package task.zorvyn.assignment.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import task.zorvyn.assignment.entity.Role;
import task.zorvyn.assignment.entity.User;
import task.zorvyn.assignment.entity.UserStatus;
import task.zorvyn.assignment.exception.ResourceNotFoundException;
import task.zorvyn.assignment.repository.UserRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User createUser(User user) {
        log.info("Service call: create user request received");
        if (user == null) {
            throw new IllegalArgumentException("User payload is required");
        }

        String trimmedUsername = user.getUsername() == null ? null : user.getUsername().trim();
        if (trimmedUsername == null || trimmedUsername.isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (userRepository.existsByUsername(trimmedUsername)) {
            throw new IllegalArgumentException("Username already exists");
        }

        user.setUsername(trimmedUsername);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(user.getRole() == null ? Role.VIEWER : user.getRole());
        user.setStatus(user.getStatus() == null ? UserStatus.ACTIVE : user.getStatus());

        return userRepository.save(user);
    }

    @Transactional
    public User assignRole(Long userId, Role role) {
        log.info("Service call: assign role={} for userId={}", role, userId);
        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }

        User existingUserEntity = getUserById(userId);
        existingUserEntity.setRole(role);
        return userRepository.save(existingUserEntity);
    }

    @Transactional
    public User changeStatus(Long userId, UserStatus status) {
        log.info("Service call: change status={} for userId={}", status, userId);
        if (status == null) {
            throw new IllegalArgumentException("Status is required");
        }

        User existingUserEntity = getUserById(userId);
        existingUserEntity.setStatus(status);
        return userRepository.save(existingUserEntity);
    }

    public List<User> getAllUsers() {
        log.info("Service call: fetch all users");
        return userRepository.findAll();
    }

    public User getUserById(Long userId) {
        log.info("Service call: fetch user by id={}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));
    }
}
