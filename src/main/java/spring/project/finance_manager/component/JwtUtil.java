package spring.project.finance_manager.component;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static io.jsonwebtoken.Jwts.builder;

@Component
public class JwtUtil {

    @Value("${secret.key}")
    private String SECRET_KEY;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    public String generateToken(String email) {
        return builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public void validateToken(String token) throws JwtException {
        Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
    }

    public String extractEmail(String token) throws JwtException {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
