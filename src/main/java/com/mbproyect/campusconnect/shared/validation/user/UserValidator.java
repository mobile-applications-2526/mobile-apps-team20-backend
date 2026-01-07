package com.mbproyect.campusconnect.shared.validation.user;

import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import com.mbproyect.campusconnect.infrastructure.repository.user.UserRepository;
import com.mbproyect.campusconnect.model.entity.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class UserValidator {

    private final UserRepository userRepository;

    public UserValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Validates that a user with the given ID exists in the database.
     * If not found, throws a UserNotFoundException.
     *
     */
    public User validateUserExists(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User with id {} not found", userId);
                    return new UserNotFoundException("User with id " + userId + " not found");
                });
    }

    /**
     * Validates that the user is active.
     */
    public void validateUserIsActive(User user) {
        if (user == null || !user.isActive()) {
            log.error("User {} is inactive or does not exist", user != null ? user.getUserId() : "unknown");
            throw new UserNotFoundException("User is inactive or not found");
        }
    }
}

