package com.mbproyect.campusconnect.service.auth;

import com.mbproyect.campusconnect.dto.auth.request.TokenRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;



@Service
@Validated
public interface ExternalAuthService {
    String verifyToken(@Valid TokenRequest token);
}