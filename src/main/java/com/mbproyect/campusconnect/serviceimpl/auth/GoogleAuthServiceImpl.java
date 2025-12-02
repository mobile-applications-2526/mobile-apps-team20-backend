package com.mbproyect.campusconnect.serviceimpl.auth;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.mbproyect.campusconnect.config.exceptions.auth.InvalidGoogleTokenException;
import com.mbproyect.campusconnect.config.exceptions.auth.InvalidTokenException;
import com.mbproyect.campusconnect.dto.auth.request.TokenRequest;
import com.mbproyect.campusconnect.service.auth.ExternalAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Service
public class GoogleAuthServiceImpl implements ExternalAuthService {

    // Inject the value from application.properties
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Override
    public String verifyToken(TokenRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                        .setAudience(Collections.singletonList(clientId)) // Use the injected variable
                        .build();

            log.info("Verifying google id...");
            GoogleIdToken idToken = verifier.verify(request.getToken());

            if (idToken == null) {
                throw new InvalidTokenException("Invalid google token provided");
            }

            return idToken.getPayload().getEmail();



        } catch (IllegalArgumentException e) {
            // Catch malformed tokens here
            log.error("Token format is invalid: {}", e.getMessage());
            throw new InvalidGoogleTokenException("Invalid token format", e);

        } catch (GeneralSecurityException | IOException e) {
            log.error("Google validation failed...");
            throw new InvalidGoogleTokenException("Google validation failed", e);
        }
    }
}