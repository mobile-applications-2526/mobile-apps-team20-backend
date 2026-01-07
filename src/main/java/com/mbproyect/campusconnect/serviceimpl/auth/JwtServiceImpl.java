package com.mbproyect.campusconnect.serviceimpl.auth;

import com.mbproyect.campusconnect.model.enums.TokenType;
import com.mbproyect.campusconnect.service.auth.JwtService;
import com.mbproyect.campusconnect.service.auth.TokenStorageService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${app.secret.key}")
    private String secretKey;

    private final TokenStorageService tokenStorageService;

    public JwtServiceImpl (TokenStorageService tokenStorageService) {
        this.tokenStorageService = tokenStorageService;
        generateSecretKey();
    }

    /**
     * Returns the private key value
     */
    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Transactional
    public void generateSecretKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacSHA256");
            SecretKey key = keyGenerator.generateKey();
            String encryptedKey =  Base64.getEncoder().encodeToString(key.getEncoded());
            System.out.println(encryptedKey); // Use one of this for secretkey enviroment
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating secret key: " + e);
        }
    }

    @Transactional
    @Override
    public String generateToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        long JWT_EXPIRATION = 1000 * 60 * TokenType.JWT.getTtlMinutes().toMinutes(); // 30 mins

        String key = TokenType.concatenate(email, TokenType.JWT);

        String token =  Jwts.builder()
                .claims(claims)
                .subject(email)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .issuedAt(new Date(System.currentTimeMillis()))
                // 30 minutes for token expiration
                .expiration( new Date(System.currentTimeMillis() + JWT_EXPIRATION) )
                .compact();

        // Save the token so it knows it is valid
        tokenStorageService.addToken(
                key, token, Duration.ofMillis(JWT_EXPIRATION)
        );

        return token;
    }

    @Override
    public String extractCredentials(String token) {
        String email =  extractClaim(token, Claims::getSubject);
        return email;
    }


    @Override
    public boolean validateToken(String token) {
        try {
            // Check if token email matches the passed email
            String tokenEmail = extractClaim(token, Claims::getSubject);

            // Check expiration (This will throw if token is forged)
            if (isTokenExpired(token)) {
                return false;
            }

            // Check storage (Stateful check)
            String key = TokenType.concatenate(tokenEmail, TokenType.JWT);
            if (!tokenStorageService.isTokenValid(key)) {
                return false;
            }

            return tokenStorageService.getToken(key).equals(token);

        } catch (Exception e) {
            // Catch SignatureException, MalformedJwtException, ExpiredJwtException
            return false;
        }
    }

    @Override
    public String extractAuthToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @Override
    public Duration tokenTtl(String token) {
        return Duration.between(
                new Date(System.currentTimeMillis()).toInstant(),
                extractExpiration(token).toInstant()
        );
    }

    /**
     * Extracts from the token the claim indicated in claimResolver
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }


    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getKey())
                .build()
                .parseSignedClaims(token)  // Verify the signature of the token
                .getPayload();
    }

    // Verifies token expiration
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date(System.currentTimeMillis()));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}