package com.mbproyect.campusconnect.controller.user;

import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.dto.chat.response.EventChatResponse;
import com.mbproyect.campusconnect.dto.user.request.UserProfileRequest;
import com.mbproyect.campusconnect.dto.user.response.UserProfileResponse;
import com.mbproyect.campusconnect.service.user.UserProfileService;
import com.mbproyect.campusconnect.service.user.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserProfileService userProfileService;
    private final UserService userService;

    public UserController(UserProfileService userProfileService, UserService userService) {
        this.userProfileService = userProfileService;
        this.userService = userService;
    }

    /**
     * Get a user profile by its ID.
     * Example: GET /api/users/{userProfileId}
     */
    @GetMapping("/{userProfileId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable UUID userProfileId) {
        UserProfileResponse response = userProfileService.getById(userProfileId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/chats")
    ResponseEntity<Page<EventChatResponse>> getUserChats(
            @RequestParam int page,
            @RequestParam int size
    ) {
        return ResponseEntity.ok(userService.getChats(page, size));
    }

    /**
     * Update user profile fields:
     * - userName
     * - nationality
     * - languages
     * - interests
     * - userLocation (city, country)
     * - age
     *
     * Example: PUT /api/users/{userProfileId}
     */
    @PutMapping("/{userProfileId}")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            @PathVariable UUID userProfileId,
            @Valid @RequestBody UserProfileRequest request
    ) {
        UserProfileResponse response = userProfileService.update(userProfileId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
   
}