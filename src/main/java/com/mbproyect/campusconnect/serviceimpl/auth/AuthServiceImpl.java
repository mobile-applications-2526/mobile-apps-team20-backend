package com.mbproyect.campusconnect.serviceimpl.auth;

import com.mbproyect.campusconnect.config.exceptions.user.InvalidTokenException;
import com.mbproyect.campusconnect.config.exceptions.user.UserAlreadyExistsException;
import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import com.mbproyect.campusconnect.dto.auth.request.RefreshTokenRequest;
import com.mbproyect.campusconnect.dto.auth.request.UserAuthRequest;
import com.mbproyect.campusconnect.dto.auth.response.UserAuthenticationResponse;
import com.mbproyect.campusconnect.events.contract.user.UserEventsNotifier;
import com.mbproyect.campusconnect.infrastructure.repository.user.UserRepository;
import com.mbproyect.campusconnect.model.entity.user.User;
import com.mbproyect.campusconnect.model.enums.TokenType;
import com.mbproyect.campusconnect.service.auth.AuthService;
import com.mbproyect.campusconnect.service.auth.JwtService;
import com.mbproyect.campusconnect.service.auth.TokenStorageService;
import com.mbproyect.campusconnect.service.user.UserService;
import com.mbproyect.campusconnect.shared.util.EncryptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final TokenStorageService tokenStorageService;

    private final UserEventsNotifier userEventsNotifier;

    private final JwtService jwtService;

    private final UserService userService;

    @Value("${app.activate.link}")
    private String baseUrl;

    public AuthServiceImpl (
            UserRepository userRepository,
            TokenStorageService tokenStorageService,
            UserEventsNotifier userEventsNotifier,
            JwtService jwtService,
            UserService userService) {
        this.userRepository = userRepository;
        this.tokenStorageService = tokenStorageService;
        this.userEventsNotifier = userEventsNotifier;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    private void validateToken (TokenType tokenType, String email, String token) {
        String key = TokenType.concatenate(
                email, tokenType
        );
        if (!tokenStorageService.isTokenValid(key)) {
            throw new InvalidTokenException("The token provided is not valid");
        }

        String savedToken = tokenStorageService.getToken(key);

        if (!token.equals(savedToken)) {
            throw new InvalidTokenException("The token provided is not valid");
        }
    }

    @Override
    public void login(UserAuthRequest userAuthRequest) {
        // Check if user exists
        User user = userRepository
                .findByEmail(userAuthRequest.getEmail())
                .orElse(null);

        // Check if the user exists
        if (user == null) return;

        // Generate verification code
        String verificationCode = EncryptionUtil.generateNumericCode(8);

        String key = TokenType.concatenate(
                user.getEmail(), TokenType.VERIFICATION_CODE
        );
        tokenStorageService.addToken(
                key, verificationCode, TokenType.VERIFICATION_CODE.getTtlMinutes()
        );

        // Send email with the token
        userEventsNotifier.onUserLoggedEvent(user.getEmail(), verificationCode);
    }

    @Override
    public void register(UserAuthRequest userAuthRequest) {
        Optional<User> user = userRepository.findByEmail(userAuthRequest.getEmail());

        // Check if user is registered already
        if (user.isPresent()) {
            throw new UserAlreadyExistsException("Email is already registered");
        }

        // Generate account activation token
        UUID activatingToken = EncryptionUtil.generateToken();
        String url = baseUrl + "?email=" + userAuthRequest.getEmail() + "&token=" + activatingToken;
        String key = TokenType
                .concatenate(userAuthRequest.getEmail(), TokenType.ACTIVATION_TOKEN);

        tokenStorageService.addToken(
                key, activatingToken.toString(), TokenType.ACTIVATION_TOKEN.getTtlMinutes()
        );

        // Send email with activation token
        userEventsNotifier.onUserRegisteredEvent(userAuthRequest.getEmail(), url);
    }

    @Override
    public UserAuthenticationResponse validateEmailCode(String verificationToken, UserAuthRequest request) {
        validateToken(
                TokenType.VERIFICATION_CODE, request.getEmail(), verificationToken
        );

        // Return successfully login
        String jwt = jwtService.generateToken(request.getEmail());
        UUID refreshToken = EncryptionUtil.generateToken();

        String key = TokenType.concatenate(request.getEmail(), TokenType.REFRESH_TOKEN);
        tokenStorageService.addToken(
                key, refreshToken.toString(), TokenType.REFRESH_TOKEN.getTtlMinutes()
        );

        // Delete code from db (token is valid for one use)
        key = TokenType.concatenate(request.getEmail(), TokenType.VERIFICATION_CODE);
        tokenStorageService.removeToken(key);

        return new UserAuthenticationResponse(
                request.getEmail(), jwt, refreshToken
        );
    }

    @Override
    public void activateAccount(String activatingToken, String email) {
        // Check if the token is valid
        validateToken(TokenType.ACTIVATION_TOKEN, email, activatingToken);

        // Token is valid so we register the user
        userService.createUser(email);
    }

    @Override
    public UserAuthenticationResponse refreshToken(RefreshTokenRequest request) {
        validateToken(
                TokenType.REFRESH_TOKEN, request.getEmail(), request.getRefreshToken()
        );

        String jwt = jwtService.generateToken(request.getEmail());
        UUID refreshToken = EncryptionUtil.generateToken();

        // Add new refresh token to db
        String key = TokenType.concatenate(request.getEmail(), TokenType.REFRESH_TOKEN);
        tokenStorageService.addToken(
                key, refreshToken.toString(), TokenType.REFRESH_TOKEN.getTtlMinutes()
        );

        return new UserAuthenticationResponse(
                request.getEmail(), jwt, refreshToken
        );
    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = jwtService.extractAuthToken(request);
        String email = jwtService.extractCredentials(token);
        // The token is not valid
        if (token == null) {
            throw new InvalidTokenException("Invalid token provided");
        }
        // Remove the jwt
        String key = TokenType.concatenate(email, TokenType.JWT);
        tokenStorageService.removeToken(key);

        // Remove also refresh token
        key = TokenType.concatenate(email, TokenType.REFRESH_TOKEN);
        tokenStorageService.removeToken(key);
    }
}
