package isuru.Technena.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import isuru.Technena.entities.user.User;
import isuru.Technena.exceptions.UnauthorizedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTTools {

    @Value("${spring.jwt.secret}")
    private String secret;

    // creating token
    public String createToken(User user) {
        return Jwts.builder().subject(String.valueOf(user.getId()))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // for a week
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    // token verification
    public void verifyToken(String token) {
        try {
            Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secret.getBytes())).build().parse(token);
        } catch (Exception ex) {
            throw new UnauthorizedException("Problems with token! Please re-try to login!");
        }
    }

    // user extraction by the token
    public String extractIdFromToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseSignedClaims(token).getPayload().getSubject();
    }
}
