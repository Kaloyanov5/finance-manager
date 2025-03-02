package spring.project.finance_manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
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
    private final UtilService utilService;

    public GeminiService(TransactionRepository transactionRepository, UserRepository userRepository,
                         JwtUtil jwtUtil, ObjectMapper objectMapper, UtilService utilService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        this.utilService = utilService;
    }

    public ResponseEntity<?> chatWithBot(String accessToken, String refreshToken,
                                         HttpServletResponse response, String userMessage) {
        try {
            accessToken = utilService.validateAndRefreshToken(accessToken, refreshToken, response);
            if (accessToken == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired tokens. Please log in again.");

            String email = jwtUtil.extractEmail(accessToken);
            User user = userRepository.findByEmail(email).get();

            List<Transaction> transactions = transactionRepository.findByUser(user);

            if (transactions.isEmpty())
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("No transactions found!");

            if (conversationHistory.isEmpty()) {
                conversationHistory.add(Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text",
                                "You are a financial assistant. " +
                                        "Provide concise and actionable financial tips, " +
                                        "keeping responses short but informative."
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

            ResponseEntity<String> aiResponse = restTemplate.exchange(
                    url + apiKey,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            GeminiResponse responseObject = objectMapper.readValue(aiResponse.getBody(), GeminiResponse.class);
            String botResponse = responseObject
                    .getCandidates()
                    .getFirst()
                    .getContent()
                    .getParts()
                    .getFirst()
                    .getText()
                    .trim();

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

    public String categorizeTransaction(String description) {
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

        try {
            GeminiResponse apiResponse = objectMapper.readValue(response.getBody(), GeminiResponse.class);
            if (apiResponse != null && !apiResponse.getCandidates().isEmpty()) {
                return apiResponse
                        .getCandidates()
                        .getFirst()
                        .getContent()
                        .getParts()
                        .getFirst()
                        .getText()
                        .trim();
            }
        } catch (JsonProcessingException e) {
            return "Error processing AI response";
        }
        return "Uncategorized";
    }
}
