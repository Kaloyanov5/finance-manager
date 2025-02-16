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
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${cohere.api.key}")
    private String apiKey;

    public TransactionService(TransactionRepository transactionRepository, RestTemplate restTemplate,
                              ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
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
                "Food, Travel, Gift, Personal.\n" +
                "\n" +
                "Examples:\n" +
                "1. \"Purchased groceries at Walmart\" → Category: Groceries\n" +
                "2. \"Dinner at a restaurant\" → Category: Food\n" +
                "3. \"Hotel stay for vacation\" → Category: Travel\n" +
                "4. \"Bought a gift for my girlfriend\" → Category: Gift\n" +
                "5. \"Paid for streaming subscription\" → Category: Entertainment\n" +
                "\n" +
                "Now, classify the following:\n" +
                "-";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String body = "{\n" +
                "  \"prompt\": \"" + prompt + " " + description + "\",\n" +
                "  \"max_tokens\": 50\n" +
                "}";

        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        CohereApiResponse apiResponse = null;
        try {
            apiResponse = objectMapper.readValue(response.getBody(), CohereApiResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        if (apiResponse != null && !apiResponse.getGenerations().isEmpty()) {
            return apiResponse.getGenerations().get(0).getText().trim();
        } else {
            return "Uncategorized";
        }
    }
}
