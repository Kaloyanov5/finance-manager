package spring.project.finance_manager.controller;

import org.apache.tomcat.util.http.parser.Authorization;
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
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }

    @GetMapping("/username")
    public ResponseEntity<?> getUsername(@RequestHeader("Authorization") String token) {
        return userService.getUsername(token);
    }
}
