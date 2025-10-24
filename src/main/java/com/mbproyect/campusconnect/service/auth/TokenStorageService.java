package com.mbproyect.campusconnect.service.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Service
@Validated
public interface TokenStorageService {

    void addToken (@NotNull String key, @NotBlank String value, Duration ttl);

    boolean isTokenValid (@NotNull String key);

    String getToken (@NotNull String key);

    void removeToken (@NotNull String key);
}