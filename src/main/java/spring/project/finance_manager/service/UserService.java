package spring.project.finance_manager.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
    private final UtilService utilService;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil, UtilService utilService) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.utilService = utilService;
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

    public ResponseEntity<?> loginUser(LoginRequest request, HttpServletResponse response) {
        String email = request.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with this email does not exist!");

        User user = optionalUser.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Incorrect password!");

        String accessToken = jwtUtil.generateAccessToken(email);
        String refreshToken = jwtUtil.generateRefreshToken(email);

        Cookie jwtCookie = new Cookie("access_token", accessToken);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(15 * 60); // 15 minutes

        Cookie refreshCookie = new Cookie("refresh_token", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(7 * 24 * 60 * 60 * 2); // 14 days

        response.addCookie(jwtCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok("Login successful");
    }

    public ResponseEntity<?> getUsername(String accessToken, String refreshToken, HttpServletResponse response) {
        accessToken = utilService.validateAndRefreshToken(accessToken, refreshToken, response);
        if (accessToken == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired tokens. Please log in again.");

        String email = jwtUtil.extractEmail(accessToken);
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with this email does not exist!");

        User user = optionalUser.get();
        return ResponseEntity.ok(user.getName());
    }

    public ResponseEntity<?> logoutUser(HttpServletResponse response) {
        utilService.resetCookies(response);
        return ResponseEntity.ok("Logged out");
    }

    public ResponseEntity<?> deleteUser(String accessToken, String refreshToken, HttpServletResponse response) {
        accessToken = utilService.validateAndRefreshToken(accessToken, refreshToken, response);
        if (accessToken == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired tokens. Please log in again.");

        String email = jwtUtil.extractEmail(accessToken);
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User with this email does not exist!");

        User user = optionalUser.get();
        userRepository.delete(user);
        utilService.resetCookies(response);
        return ResponseEntity.ok("User deleted successfully");
    }
}
