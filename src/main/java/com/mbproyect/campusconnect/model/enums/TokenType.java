package com.mbproyect.campusconnect.model.enums;

public enum TokenType {
    JWT,
    ACTIVATION_CODE,
    VERIFICATION_CODE;

    public static String concatenate(String value, TokenType tokenStorage) {
        return tokenStorage + value;
    }
}

