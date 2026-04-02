package task.zorvyn.assignment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import task.zorvyn.assignment.dto.UserRequestDTO;
import task.zorvyn.assignment.dto.UserResponseDTO;
import task.zorvyn.assignment.entity.Role;
import task.zorvyn.assignment.entity.User;
import task.zorvyn.assignment.entity.UserStatus;
import task.zorvyn.assignment.service.UserService;

import java.util.List;

/**
 * User management endpoints.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO request) {
        User created = userService.createUser(toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers() {
        List<UserResponseDTO> users = userService.getAllUsers().stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(userService.getUserById(id)));
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponseDTO> assignRole(@PathVariable Long id, @RequestParam Role role) {
        return ResponseEntity.ok(toResponse(userService.assignRole(id, role)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponseDTO> changeStatus(@PathVariable Long id, @RequestParam UserStatus status) {
        return ResponseEntity.ok(toResponse(userService.changeStatus(id, status)));
    }

    private User toEntity(UserRequestDTO request) {
        return User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .role(request.getRole())
                .status(request.getStatus())
                .build();
    }

    private UserResponseDTO toResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
