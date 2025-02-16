package spring.project.finance_manager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import spring.project.finance_manager.entity.Transaction;
import spring.project.finance_manager.repository.TransactionRepository;
import spring.project.finance_manager.request.CohereApiResponse;
import spring.project.finance_manager.request.TransactionRequest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${cohere.api.key}")
    private String apiKey;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public ResponseEntity<?> saveTransaction(TransactionRequest request) {
        Transaction transaction = new Transaction();
        String requestDescription = request.getDescription();
        String category = categorizeTransaction(requestDescription);

        transaction.setDescription(requestDescription);
        transaction.setAmount(request.getAmount());
        transaction.setDate(request.getDate() == null ? Date.valueOf(LocalDate.now()) : request.getDate());
        transaction.setCategory(category);

        transactionRepository.save(transaction);
        return ResponseEntity.ok("Transaction saved successfully!");
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public String categorizeTransaction(String description) {
        String url = "https://api.cohere.ai/v1/generate";

        String prompt = "Please categorize the following transaction descriptions into the best category. " +
                "Choose from the following categories: Groceries, Rent, Entertainment, Shopping, " +
                "Food, Travel, Gift, Personal.\n\n" +
                "Examples:\n" +
                "1. \"Purchased groceries at Walmart\" → Category: Groceries\n" +
                "2. \"Dinner at a restaurant\" → Category: Food\n" +
                "3. \"Hotel stay for vacation\" → Category: Travel\n" +
                "4. \"Bought a gift for my girlfriend\" → Category: Gift\n" +
                "5. \"Paid for streaming subscription\" → Category: Entertainment\n\n" +
                "Now, classify the following:\n" +
                "- " + description;

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("prompt", prompt);
        requestBody.put("max_tokens", 50);

        String body;
        try {
            body = objectMapper.writeValueAsString(requestBody);
        } catch (JsonProcessingException e) {
            return "Error creating JSON request";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        } catch (Exception e) {
            return "Error communicating with AI API";
        }

        try {
            CohereApiResponse apiResponse = objectMapper.readValue(response.getBody(), CohereApiResponse.class);
            if (apiResponse != null && !apiResponse.getGenerations().isEmpty()) {
                return apiResponse.getGenerations().get(0).getText().trim().split(" ")[1]
                        .replace(".", "").trim();
            }
        } catch (JsonProcessingException e) {
            return "Error processing AI response";
        }

        return "Uncategorized";
    }
}
