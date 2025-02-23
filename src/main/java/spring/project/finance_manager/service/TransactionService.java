package spring.project.finance_manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import spring.project.finance_manager.component.JwtUtil;
import spring.project.finance_manager.entity.Transaction;
import spring.project.finance_manager.entity.User;
import spring.project.finance_manager.repository.TransactionRepository;
import spring.project.finance_manager.repository.UserRepository;
import spring.project.finance_manager.component.GeminiResponse;
import spring.project.finance_manager.request.TransactionRequest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JwtUtil jwtUtil;

    @Value("${gemini.api.key}")
    private String apiKey;

    public TransactionService(TransactionRepository transactionRepository,
                              UserRepository userRepository, JwtUtil jwtUtil) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> saveTransaction(String token, TransactionRequest request) {
        String email;
        try {
            email = jwtUtil.extractEmail(token.substring(7));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token!");
        }
        User user = userRepository.findByEmail(email).get();

        String requestDescription = request.getDescription();
        String category = categorizeTransaction(requestDescription);

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

    public ResponseEntity<?> getAllTransactions(String token) {
        String email;
        try {
            email = jwtUtil.extractEmail(token.substring(7));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token!");
        }
        User user = userRepository.findByEmail(email).get();
        return ResponseEntity.ok(transactionRepository.findByUser(user));
    }

    public ResponseEntity<?> deleteTransaction(String token, Long id) {
        try {
            jwtUtil.validateToken(token.substring(7));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token!");
        }

        if (!transactionRepository.findById(id).isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Transaction not found!");

        transactionRepository.deleteById(id);
        return ResponseEntity.ok("Transaction removed successfully!");
    }

    public String categorizeTransaction(String description) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

        String prompt = "Please categorize the following transaction descriptions into the best category. " +
                "Choose from the following categories: Groceries, Rent, Entertainment, Shopping, " +
                "Food, Travel, Gift, Personal, Savings.\n\n" +
                "Examples:\n" +
                "1. \"Purchased groceries at Walmart\" → Category: Groceries\n" +
                "2. \"Dinner at a restaurant\" → Category: Food\n" +
                "3. \"Hotel stay for vacation\" → Category: Travel\n" +
                "4. \"Bought a gift for my girlfriend\" → Category: Gift\n" +
                "5. \"Paid for streaming subscription\" → Category: Entertainment\n" +
                "6. \"Added to savings account\" → Category: Savings\n\n" +
                "I want your answer to be just one word, which is just the category itself." +
                "Now, classify the following:\n" +
                "- " + description;

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String safePrompt = prompt.replace("\"", "\\\"");

        String requestBody = "{\n" +
                "  \"contents\": [{\n" +
                "    \"parts\": [{\"text\": \"" + safePrompt + "\"}]\n" +
                "  }]\n" +
                "}";

        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url + apiKey,
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());

        try {
            GeminiResponse apiResponse = objectMapper.readValue(response.getBody(), GeminiResponse.class);
            if (apiResponse != null && !apiResponse.getCandidates().isEmpty()) {
                return apiResponse.getCandidates().get(0).getContent().getParts().get(0).getText().trim();
            }
        } catch (JsonProcessingException e) {
            return "Error processing AI response";
        }
        return "Uncategorized";
    }

}
