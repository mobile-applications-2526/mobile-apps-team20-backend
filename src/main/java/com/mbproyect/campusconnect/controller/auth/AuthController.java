package com.mbproyect.campusconnect.controller.auth;

import com.mbproyect.campusconnect.dto.auth.request.RefreshTokenRequest;
import com.mbproyect.campusconnect.dto.auth.request.UserAuthRequest;
import com.mbproyect.campusconnect.dto.auth.response.UserAuthenticationResponse;
import com.mbproyect.campusconnect.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     *  If the email provided is in the db, an email is sent with a verification code
     *  Returns the refresh token and a header token
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserAuthRequest userAuthRequest) {
        String response = authService.login(userAuthRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-code")
    public ResponseEntity<UserAuthenticationResponse> validateEmailCode(
            @RequestParam String verificationCode,
            @RequestBody UserAuthRequest request
    ) {
        UserAuthenticationResponse response = authService.validateEmailCode(verificationCode, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Send an activate link with a token to the email
     */
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserAuthRequest request) {
        authService.register(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/activate-account")
    public ResponseEntity<String> activateAccount(
            @RequestParam String emailToken,
            @RequestBody UserAuthRequest request
    ) {
        authService.activateAccount(emailToken, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<UserAuthenticationResponse> refreshToken(
            @RequestBody RefreshTokenRequest request
    ) {
        UserAuthenticationResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    /**
     *  The backend invalidates the header token & refresh token
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

}
