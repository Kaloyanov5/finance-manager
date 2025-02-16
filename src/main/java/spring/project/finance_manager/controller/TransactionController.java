package spring.project.finance_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.finance_manager.entity.Transaction;
import spring.project.finance_manager.request.TransactionRequest;
import spring.project.finance_manager.service.TransactionService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public List<Transaction> getTransactions() {
        return transactionService.getAllTransactions();
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> saveTransaction(@RequestBody TransactionRequest request) {
        return transactionService.saveTransaction(request);
    }
}
