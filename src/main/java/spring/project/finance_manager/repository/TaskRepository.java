package spring.project.finance_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.finance_manager.entity.Task;
import spring.project.finance_manager.entity.User;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUser(User user);
    void deleteById(Long id);
}
