package spring.project.finance_manager.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.project.finance_manager.request.LoginRequest;
import spring.project.finance_manager.request.RegisterRequest;
import spring.project.finance_manager.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request, HttpServletResponse response) {
        return userService.loginUser(request, response);
    }

    @GetMapping("/username")
    public ResponseEntity<?> getUsername(@CookieValue(name = "access_token", required = false) String accessToken,
                                         @CookieValue(name = "refresh_token", required = false) String refreshToken,
                                         HttpServletResponse response) {
        return userService.getUsername(accessToken, refreshToken, response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        return userService.logoutUser(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@CookieValue(name = "access_token", required = false) String accessToken,
                                        @CookieValue(name = "refresh_token", required = false) String refreshToken,
                                        HttpServletResponse response) {
        return userService.deleteUser(accessToken, refreshToken, response);
    }
}
