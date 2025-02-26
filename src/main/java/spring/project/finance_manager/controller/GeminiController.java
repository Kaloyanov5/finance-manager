package spring.project.finance_manager.controller;

import jakarta.servlet.http.HttpServletResponse;
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
    public ResponseEntity<?> chatWithBot(@CookieValue(name = "access_token", required = false) String accessToken,
                                         @CookieValue(name = "refresh_token", required = false) String refreshToken,
                                         HttpServletResponse response, @RequestBody String userMessage) {
        return geminiService.chatWithBot(accessToken, refreshToken, response, userMessage);
    }
}
