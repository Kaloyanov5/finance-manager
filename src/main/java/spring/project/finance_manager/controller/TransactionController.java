package spring.project.finance_manager.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<?> getTransactions(@CookieValue(name = "access_token", required = false) String accessToken,
                                             @CookieValue(name = "refresh_token", required = false) String refreshToken,
                                             HttpServletResponse response) {
        return transactionService.getAllTransactions(accessToken, refreshToken, response);
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> saveTransaction(@CookieValue(name = "access_token", required = false) String accessToken,
                                             @CookieValue(name = "refresh_token", required = false) String refreshToken,
                                             HttpServletResponse response,
                                             @RequestBody TransactionRequest transactionRequest) {
        return transactionService.saveTransaction(accessToken, refreshToken, response, transactionRequest);
    }

    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<?> deleteTransaction(@CookieValue(name = "access_token", required = false) String accessToken,
                                               @CookieValue(name = "refresh_token", required = false) String refreshToken,
                                               HttpServletResponse response, @PathVariable Long id) {
        return transactionService.deleteTransaction(accessToken, refreshToken, response, id);
    }
}
