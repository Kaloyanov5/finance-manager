package spring.project.finance_manager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.finance_manager.service.GeminiService;

@RestController
@RequestMapping("/chat")
public class GeminiController {

    private final GeminiService geminiService;

    public GeminiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping
    public ResponseEntity<?> chatWithBot(@RequestHeader("Authorization") String token, @RequestBody String userMessage) {
        return geminiService.chatWithBot(token, userMessage);
    }
}
