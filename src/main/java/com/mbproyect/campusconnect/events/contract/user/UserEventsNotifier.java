package com.mbproyect.campusconnect.events.contract.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public interface UserEventsNotifier {

    void onUserRegisteredEvent(@Email @NotNull String email, String activateLink);

    void onUserLoggedEvent(
            @Email @NotBlank String email,
            @NotBlank String verificationCode
    );
}
