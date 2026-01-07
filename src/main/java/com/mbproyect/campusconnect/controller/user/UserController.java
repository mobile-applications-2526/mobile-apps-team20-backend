package com.mbproyect.campusconnect.controller.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbproyect.campusconnect.dto.chat.response.EventChatResponse;
import com.mbproyect.campusconnect.dto.user.request.UserProfileRequest;
import com.mbproyect.campusconnect.dto.user.response.UserProfileResponse;
import com.mbproyect.campusconnect.service.user.UserProfileService;
import com.mbproyect.campusconnect.service.user.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class UserController {

    private final UserProfileService userProfileService;
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController(UserProfileService userProfileService, UserService userService, ObjectMapper objectMapper) {
        this.userProfileService = userProfileService;
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    /**
     * Get a user profile by its ID.
     * Example: GET /api/users/{userProfileId}
     */
    @GetMapping("/{userProfileId}")
    public ResponseEntity<UserProfileResponse> getUserProfileById(@PathVariable UUID userProfileId) {
        UserProfileResponse response = userProfileService.getById(userProfileId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfileByUsername(@PathVariable String username) {
        UserProfileResponse response = userProfileService
                .getByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/chats")
    ResponseEntity<Page<EventChatResponse>> getUserChats(
            @RequestParam int page,
            @RequestParam int size
    ) {
        var response = userService.getChats(page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/location")
    ResponseEntity<String> getUserLocation() {
        return ResponseEntity.ok(userService.getUserLocation());
    }

    /**
     * Update user profile fields:
     * - userName
     * - nationality
     * - languages
     * - interests
     * - userLocation (city, country)
     * - age
     * - profile picture (optional image file)
     *
     * Example: PUT /api/user/{userProfileId}
     * Content-Type: multipart/form-data with parts:
     *  - data: JSON for UserProfileRequest
     *  - image: image file (optional)
     */
        @PutMapping(value = "/{userProfileId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
        public ResponseEntity<UserProfileResponse> updateUserProfile(
            @PathVariable UUID userProfileId,
            @RequestPart("data") String userProfileRequestStr,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
        ) throws JsonProcessingException {

        UserProfileRequest request = objectMapper
            .readValue(userProfileRequestStr, UserProfileRequest.class);

        UserProfileResponse response = userProfileService.update(userProfileId, request, imageFile);
        return ResponseEntity.status(HttpStatus.OK).body(response);
        }
    
   
}