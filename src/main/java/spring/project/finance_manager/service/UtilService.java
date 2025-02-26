package spring.project.finance_manager.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import spring.project.finance_manager.component.JwtUtil;
import spring.project.finance_manager.entity.User;
import spring.project.finance_manager.repository.UserRepository;

import java.util.Optional;

@Service
public class UtilService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UtilService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public void resetCookies(HttpServletResponse response) {
        Cookie accessCookie = new Cookie("access_token", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie("refresh_token", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);
    }

    public String refreshAccessToken(String refreshToken, HttpServletResponse response) {
        if (!jwtUtil.validateToken(refreshToken))
            return null;

        String email = jwtUtil.extractEmail(refreshToken);
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty())
            return null;

        String newAccessToken = jwtUtil.generateAccessToken(optionalUser.get().getEmail());

        Cookie newAccessCookie = new Cookie("access_token", newAccessToken);
        newAccessCookie.setHttpOnly(true);
        newAccessCookie.setSecure(true);
        newAccessCookie.setPath("/");
        newAccessCookie.setMaxAge(15 * 60); // 15 minutes (method retrieves parameter as seconds)

        response.addCookie(newAccessCookie);

        return newAccessToken;
    }
}
