package spring.project.finance_manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.project.finance_manager.entity.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
