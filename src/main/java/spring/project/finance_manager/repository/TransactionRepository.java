package spring.project.finance_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.finance_manager.entity.Transaction;
import spring.project.finance_manager.entity.User;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUser(User user);
    void deleteById(Long id);
}
