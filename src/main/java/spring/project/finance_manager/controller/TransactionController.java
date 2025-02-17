package spring.project.finance_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.finance_manager.entity.Transaction;
import spring.project.finance_manager.request.TransactionRequest;
import spring.project.finance_manager.service.TransactionService;

@RestController
@RequestMapping("/api")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions(@RequestHeader("Authorization") String token) {
        return transactionService.getAllTransactions(token);
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> saveTransaction(@RequestHeader("Authorization") String token,
                                             @RequestBody TransactionRequest request) {
        return transactionService.saveTransaction(token, request);
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<?> deleteTransaction(@RequestHeader("Authorization") String token,
                                               @PathVariable Long id) {
        return transactionService.deleteTransaction(token, id);
    }
}
