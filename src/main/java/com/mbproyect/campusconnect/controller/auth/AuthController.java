package com.mbproyect.campusconnect.controller.auth;

import com.mbproyect.campusconnect.dto.auth.request.UserAuthRequest;
import com.mbproyect.campusconnect.dto.auth.response.UserAuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {


    /**
     *  If the email provided is in the db, an email is sent with a verification code
     *  Returns the refresh token and a header token
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserAuthRequest userAuthRequest) {
        return ResponseEntity.ok("Email code was sent");
    }

    @PostMapping("/validate-code")
    public ResponseEntity<String> validateEmailCode(String code) {
        return ResponseEntity.ok("Header token & refresh token");
    }

    /**
     * Send an activate link with a token to the email
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(String email) {
        return ResponseEntity.ok("If email exists, use the link to activate the account");
    }

    @PostMapping("/activate-account")
    public ResponseEntity<String> activateAccount(String emailToken) {
        return ResponseEntity.ok("Account activated");
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshToken(String refreshToken) {
        return ResponseEntity.ok("header-token");
    }

    /**
     *  The backend invalidates the header token & refresh token
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        return ResponseEntity.noContent().build();
    }

}
