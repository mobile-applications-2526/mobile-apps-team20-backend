package com.mbproyect.campusconnect.model.enums;

import lombok.Getter;

import java.time.Duration;

@Getter
public enum TokenType {
    JWT(30),
    ACTIVATION_TOKEN(60*24),
    VERIFICATION_CODE(10),
    REFRESH_TOKEN(60*24*30); // 30 days

    private final Duration ttlMinutes;

    TokenType(long ttlMinutes) {
        this.ttlMinutes = Duration.ofMinutes(ttlMinutes);
    }

    public static String concatenate(String value, TokenType tokenStorage) {
        return tokenStorage + value;
    }
}

