package com.mbproyect.campusconnect.service.user;

import com.mbproyect.campusconnect.dto.user.request.UserProfileRequest;
import com.mbproyect.campusconnect.dto.user.response.UserProfileResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@Validated
public interface UserProfileService {

    UserProfileResponse getById(@NotNull UUID userProfileId);

    UserProfileResponse getByUsername(@NotNull String username);

        UserProfileResponse update(
            @NotNull UUID userProfileId,
            @Valid UserProfileRequest request,
            MultipartFile profileImage
        );

    
}