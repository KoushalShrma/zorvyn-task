package task.zorvyn.assignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import task.zorvyn.assignment.entity.Role;
import task.zorvyn.assignment.entity.UserStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDTO {
    private Long id;
    private String username;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
}
