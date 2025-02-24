package spring.project.finance_manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import spring.project.finance_manager.component.GeminiResponse;
import spring.project.finance_manager.component.JwtUtil;
import spring.project.finance_manager.entity.Transaction;
import spring.project.finance_manager.entity.User;
import spring.project.finance_manager.repository.TransactionRepository;
import spring.project.finance_manager.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private final String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";
    @Value("${gemini.api.key}")
    private String apiKey;

    private final List<Map<String, Object>> conversationHistory = new ArrayList<>();
    private final RestTemplate restTemplate = new RestTemplate();

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public GeminiService(TransactionRepository transactionRepository, UserRepository userRepository,
                         JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<?> chatWithBot(String token, String userMessage) {
        try {
            String email;
            try {
                email = jwtUtil.extractEmail(token.substring(7));
            } catch (JwtException e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token!");
            }

            User user = userRepository.findByEmail(email).get();
            List<Transaction> transactions = transactionRepository.findByUser(user);

            if (transactions.isEmpty())
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("No transactions found!");

            if (conversationHistory.isEmpty()) {
                conversationHistory.add(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text",
                                "You are a financial assistant. Provide concise and actionable financial tips, keeping responses short but informative."
                        ))
                ));
            }

            StringBuilder transactionContext = new StringBuilder("Here are my recent transactions: ");
            for (Transaction t : transactions) {
                transactionContext.append(t.getDescription()).append(": $").append(t.getAmount()).append(", ");
            }

            conversationHistory.add(Map.of(
                    "role", "user",
                    "parts", List.of(Map.of("text", transactionContext.toString() + " " + userMessage))
            ));


            Map<String, Object> requestPayload = Map.of("contents", conversationHistory);
            String requestBody = objectMapper.writeValueAsString(requestPayload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url + apiKey,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            GeminiResponse responseObject = objectMapper.readValue(response.getBody(), GeminiResponse.class);
            String botResponse = responseObject.getCandidates().get(0).getContent().getParts().get(0).getText().trim();
            if (botResponse == null || botResponse.isBlank()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No response from AI!");
            }

            conversationHistory.add(Map.of(
                    "role", "model",
                    "parts", List.of(Map.of("text", botResponse))
            ));

            return ResponseEntity.ok(botResponse);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
}
