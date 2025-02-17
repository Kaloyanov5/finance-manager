package spring.project.finance_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.finance_manager.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
