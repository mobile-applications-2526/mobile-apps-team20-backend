package com.mbproyect.campusconnect.service.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public interface JwtService {

    /**
     * Generates a token with the proportionate key
     */
    String generateToken(String email);

    /**
     * Takes the username from the token
     */
    String extractCredentials(String token);

    /**
     *  Token validation
     *  Looks for date expiration and valid username
     */
    boolean validateToken(String token);

    String extractAuthToken(HttpServletRequest request);

    Duration tokenTtl(String token);

}
