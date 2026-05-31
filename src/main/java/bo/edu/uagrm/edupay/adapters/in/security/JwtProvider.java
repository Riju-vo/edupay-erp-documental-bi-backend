package bo.edu.uagrm.edupay.adapters.in.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    // Hardcoded simple secret for demo purposes. In production, use environment variables.
    private final SecretKey key = Keys.hmacShaKeyFor("a-very-secure-secret-key-that-must-be-at-least-256-bits-long!".getBytes());
    private final long validityInMilliseconds = 3600000; // 1h

    public String createToken(String erpCode, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .subject(erpCode)
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }
}
