package com.mbproyect.campusconnect.serviceimpl.auth;

import com.mbproyect.campusconnect.config.exceptions.auth.InvalidTokenException;
import com.mbproyect.campusconnect.config.exceptions.user.UserAlreadyExistsException;
import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import com.mbproyect.campusconnect.dto.auth.request.RefreshTokenRequest;
import com.mbproyect.campusconnect.dto.auth.request.TokenRequest;
import com.mbproyect.campusconnect.dto.auth.request.UserAuthRequest;
import com.mbproyect.campusconnect.dto.auth.response.UserAuthenticationResponse;
import com.mbproyect.campusconnect.events.contract.user.UserEventsNotifier;
import com.mbproyect.campusconnect.infrastructure.repository.user.UserRepository;
import com.mbproyect.campusconnect.model.entity.user.User;
import com.mbproyect.campusconnect.model.enums.TokenType;
import com.mbproyect.campusconnect.service.auth.AuthService;
import com.mbproyect.campusconnect.service.auth.JwtService;
import com.mbproyect.campusconnect.service.auth.ExternalAuthService;
import com.mbproyect.campusconnect.service.auth.TokenStorageService;
import com.mbproyect.campusconnect.service.user.UserService;
import com.mbproyect.campusconnect.shared.util.EncryptionUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    private final ExternalAuthService externalAuthService;

    @Value("${app.activate.link}")
    private String baseUrl;

    public AuthServiceImpl (
            UserRepository userRepository,
            TokenStorageService tokenStorageService,
            UserEventsNotifier userEventsNotifier,
            JwtService jwtService,
            UserService userService,
            ExternalAuthService externalAuthService) {
        this.userRepository = userRepository;
        this.tokenStorageService = tokenStorageService;
        this.userEventsNotifier = userEventsNotifier;
        this.jwtService = jwtService;
        this.userService = userService;
        this.externalAuthService = externalAuthService;
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

    // Helper to generate user tokens when login
    private UserAuthenticationResponse generateAuthTokens (
            String email,
            String username
    ) {
        String jwt = jwtService.generateToken(email);
        UUID refreshToken = EncryptionUtil.generateToken();

        String key = TokenType.concatenate(email, TokenType.REFRESH_TOKEN);
        tokenStorageService.addToken(
                key, refreshToken.toString(), TokenType.REFRESH_TOKEN.getTtlMinutes()
        );

        return new UserAuthenticationResponse(
                email,
                username,
                jwt,
                refreshToken
        );
    }


    @Transactional
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

    @Transactional
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

    @Transactional
    @Override
    public UserAuthenticationResponse validateEmailCode(String verificationToken, UserAuthRequest request) {
        validateToken(
                TokenType.VERIFICATION_CODE,
                request.getEmail(),
                verificationToken
        );

        Optional<User> user = userService.findUserByEmail(request.getEmail());

        if (user.isEmpty()) {
            throw new UserNotFoundException("User nor found");
        }

        UserAuthenticationResponse response = this
                .generateAuthTokens(
                        request.getEmail(),
                        user.get().getUserProfile().getUserName()
                );

        // Delete code from db (token is valid for one use)
        String key = TokenType
                .concatenate(request.getEmail(), TokenType.VERIFICATION_CODE);

        tokenStorageService.removeToken(key);

        return response;
    }

    @Transactional
    @Override
    public void activateAccount(String activatingToken, String email) {
        // Check if the token is valid
        validateToken(TokenType.ACTIVATION_TOKEN, email, activatingToken);

        // Token is valid so we register the user
        userService.createUser(email);
    }

    @Transactional
    @Override
    public UserAuthenticationResponse tokenAuthentication(TokenRequest request) {
        // Validate token
        String email = externalAuthService.verifyToken(request);

        // Check if user already exists
        Optional<User> user = userRepository.findByEmail(email);

        User newUser = null;
        if (user.isEmpty()) {
            newUser = userService.createUser(email);
        }

        // Generate access token
        return this.generateAuthTokens(
                email,
                user.isEmpty()
                        ? newUser.getUserProfile().getUserName()
                        : user.get().getUserProfile().getUserName()
        );
    }

    @Transactional
    @Override
    public UserAuthenticationResponse refreshToken(RefreshTokenRequest request) {
        validateToken(
                TokenType.REFRESH_TOKEN, request.getEmail(), request.getRefreshToken()
        );

        Optional<User> user = userRepository.findByEmail(request.getEmail());

        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        String jwt = jwtService.generateToken(request.getEmail());
        UUID refreshToken = EncryptionUtil.generateToken();

        // Add new refresh token to db
        String key = TokenType.concatenate(request.getEmail(), TokenType.REFRESH_TOKEN);
        tokenStorageService.addToken(
                key, refreshToken.toString(), TokenType.REFRESH_TOKEN.getTtlMinutes()
        );

        return new UserAuthenticationResponse(
                request.getEmail(),
                user.get().getUserProfile().getUserName(),
                jwt,
                refreshToken
        );
    }

    @Transactional
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
