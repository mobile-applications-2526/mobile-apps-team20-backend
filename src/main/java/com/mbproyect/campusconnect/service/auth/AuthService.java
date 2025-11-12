package com.mbproyect.campusconnect.service.auth;

import com.mbproyect.campusconnect.dto.auth.request.RefreshTokenRequest;
import com.mbproyect.campusconnect.dto.auth.request.UserAuthRequest;
import com.mbproyect.campusconnect.dto.auth.response.UserAuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public interface AuthService {

    void login(@Valid UserAuthRequest userAuthRequest);

    void register(@Valid UserAuthRequest userAuthRequest);

    UserAuthenticationResponse validateEmailCode(
            @NotBlank String code,
            @Valid UserAuthRequest request
    );

    void activateAccount(
            @NotBlank String activatingToken,
            @NotBlank String email
    );

    UserAuthenticationResponse refreshToken(@Valid RefreshTokenRequest request);

    void logout(HttpServletRequest request);

}
