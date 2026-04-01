package task.zorvyn.assignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import task.zorvyn.assignment.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
