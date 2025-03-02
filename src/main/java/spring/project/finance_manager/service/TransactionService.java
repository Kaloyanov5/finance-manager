package spring.project.finance_manager.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import spring.project.finance_manager.component.JwtUtil;
import spring.project.finance_manager.entity.Transaction;
import spring.project.finance_manager.entity.User;
import spring.project.finance_manager.repository.TransactionRepository;
import spring.project.finance_manager.repository.UserRepository;
import spring.project.finance_manager.request.TransactionRequest;

import java.time.LocalDate;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final UtilService utilService;
    private final GeminiService geminiService;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository, JwtUtil jwtUtil, UtilService utilService,
                              GeminiService geminiService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.utilService = utilService;
        this.geminiService = geminiService;
    }

    public ResponseEntity<?> saveTransaction(String accessToken, String refreshToken,
                                             HttpServletResponse response, TransactionRequest request) {
        accessToken = utilService.validateAndRefreshToken(accessToken, refreshToken, response);
        if (accessToken == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired tokens. Please log in again.");

        String email = jwtUtil.extractEmail(accessToken);
        User user = userRepository.findByEmail(email).get();

        String requestDescription = request.getDescription();
        String category = geminiService.categorizeTransaction(requestDescription);

        Transaction transaction = new Transaction(
                requestDescription,
                request.getAmount(),
                request.getDate() == null ? LocalDate.now() : request.getDate(),
                category,
                user
        );

        transactionRepository.save(transaction);
        return ResponseEntity.ok("Transaction saved successfully!");
    }

    public ResponseEntity<?> getAllTransactions(String accessToken, String refreshToken, HttpServletResponse response) {
        accessToken = utilService.validateAndRefreshToken(accessToken, refreshToken, response);
        if (accessToken == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired tokens. Please log in again.");

        String email = jwtUtil.extractEmail(accessToken);
        User user = userRepository.findByEmail(email).get();
        return ResponseEntity.ok(transactionRepository.findByUser(user));
    }

    public ResponseEntity<?> deleteTransaction(String accessToken, String refreshToken,
                                               HttpServletResponse response, Long id) {
        accessToken = utilService.validateAndRefreshToken(accessToken, refreshToken, response);
        if (accessToken == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired tokens. Please log in again.");

        if (transactionRepository.findById(id).isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found!");

        transactionRepository.deleteById(id);
        return ResponseEntity.ok("Transaction removed successfully!");
    }
}
