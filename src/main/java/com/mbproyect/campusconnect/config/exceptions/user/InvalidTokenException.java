package com.mbproyect.campusconnect.config.exceptions.user;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message) {
        super(message);
    }
}
