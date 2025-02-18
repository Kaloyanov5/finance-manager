package spring.project.finance_manager.service;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.project.finance_manager.component.AuthResponse;
import spring.project.finance_manager.component.JwtUtil;
import spring.project.finance_manager.entity.User;
import spring.project.finance_manager.repository.UserRepository;
import spring.project.finance_manager.request.LoginRequest;
import spring.project.finance_manager.request.RegisterRequest;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> registerUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent())
            return ResponseEntity.badRequest().body("Email is already in use!");

        if (!request.getPassword().equals(request.getConfirmPassword()))
            return ResponseEntity.badRequest().body("Passwords do not match!");

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                new ArrayList<>()
        );
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    public ResponseEntity<?> loginUser(LoginRequest request) {
        if (!userRepository.findByEmail(request.getEmail()).isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with this email does not exist!");

        User user = userRepository.findByEmail(request.getEmail()).get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password!");

        String token = jwtUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthResponse(token));
    }

    public ResponseEntity<?> getUsername(String token) {
        try {
            jwtUtil.validateToken(token.substring(7));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token!");
        }

        String email = jwtUtil.extractEmail(token.substring(7));
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (!optionalUser.isPresent())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with this email does not exist!");

        return ResponseEntity.ok(optionalUser.get().getName());
    }
}
