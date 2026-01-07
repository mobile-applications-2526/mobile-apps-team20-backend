package com.mbproyect.campusconnect.service.user;

import com.mbproyect.campusconnect.dto.chat.response.EventChatResponse;
import com.mbproyect.campusconnect.model.entity.user.User;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    // Extracts current's user email from security context
    String getCurrentUser();

    void validateCurrentUser(@NotBlank String email);

    Page<EventChatResponse> getChats(
            @Min(0) int page,
            @Min(1) @Max(50) int size
    );

    User createUser(@NotBlank String email);

    Optional<User> findUserByEmail (String email);

    String getUserLocation();
}
