package com.mbproyect.campusconnect.serviceimpl.user;

import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import com.mbproyect.campusconnect.dto.user.request.UserProfileRequest;
import com.mbproyect.campusconnect.dto.user.response.UserProfileResponse;
import com.mbproyect.campusconnect.infrastructure.mappers.user.UserProfileMapper;
import com.mbproyect.campusconnect.infrastructure.repository.user.UserProfileRepository;
import com.mbproyect.campusconnect.model.entity.user.UserLocation;
import com.mbproyect.campusconnect.model.entity.user.UserProfile;
import com.mbproyect.campusconnect.service.user.UserProfileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileServiceImpl(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    private String uploadProfileImage(MultipartFile file) {
        try {
            String UPLOAD_DIR = "uploads/profiles/";
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);

            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(filename);

            file.transferTo(filePath);

            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store profile image", e);
        }
    }

    @Transactional(readOnly = true)
    @Override
    public UserProfileResponse getById(UUID userProfileId) {
        UserProfile profile = userProfileRepository.findById(userProfileId)
                .orElseThrow(() -> new UserNotFoundException("UserProfile with id " + userProfileId + " not found"));
        return UserProfileMapper.toResponse(profile);
    }

    @Transactional(readOnly = true)
    @Override
    public UserProfileResponse getByUsername(String username) {
        UserProfile profile = userProfileRepository
                .findByUserName(username)
                .orElseThrow(
                        () -> new UserNotFoundException("UserProfile with username" + username + " not found")
                );
        return UserProfileMapper.toResponse(profile);
    }

    @Override
    @Transactional
    public UserProfileResponse update(UUID userProfileId, UserProfileRequest request, MultipartFile profileImage) {
        UserProfile profile = userProfileRepository.findById(userProfileId)
                .orElseThrow(() -> new UserNotFoundException("UserProfile with id " + userProfileId + " not found"));

        boolean changed = false;

        if (!Objects.equals(profile.getUserName(), request.getUserName())) {
            profile.setUserName(request.getUserName());
            changed = true;
        }
        if (!Objects.equals(profile.getNationality(), request.getNationality())) {
            profile.setNationality(request.getNationality());
            changed = true;
        }
        if (!Objects.equals(profile.getAge(), request.getAge())) {
            profile.setAge(request.getAge());
            changed = true;
        }

        // Bio
        if (!Objects.equals(profile.getBio(), request.getBio())) {
            profile.setBio(request.getBio());
            changed = true;
        }

        // Languages
        if (!Objects.equals(profile.getLanguages(), request.getLanguages())) {
            profile.setLanguages(request.getLanguages() != null ? new HashSet<>(request.getLanguages()) : new HashSet<>());
            changed = true;
        }

        // Interests
        if (!Objects.equals(profile.getInterests(), request.getInterests())) {
            profile.setInterests(request.getInterests() != null ? new HashSet<>(request.getInterests()) : new HashSet<>());
            changed = true;
        }

        // Location (compare by fields)
        UserLocation reqLoc = request.getUserLocation();
        UserLocation currLoc = profile.getUserLocation();

        boolean locationChanged = (reqLoc == null && currLoc != null)
                || (reqLoc != null && currLoc == null)
                || (reqLoc != null && currLoc != null
                    && (!Objects.equals(currLoc.getCity(), reqLoc.getCity())
                        || !Objects.equals(currLoc.getCountry(), reqLoc.getCountry())));

        if (locationChanged) {
            profile.setUserLocation(reqLoc == null ? null : new UserLocation(reqLoc.getCity(), reqLoc.getCountry()));
            changed = true;
        }

        // Profile picture: if a new image file is provided, upload and set its filename
        if (profileImage != null && !profileImage.isEmpty()) {
            String newImagePath = uploadProfileImage(profileImage);
            if (!Objects.equals(profile.getProfilePicture(), newImagePath)) {
                profile.setProfilePicture(newImagePath);
                changed = true;
            }
        }

        if (changed) {
            profile = userProfileRepository.save(profile);
            log.info("Updated user profile {}", userProfileId);
        } else {
            log.info("No changes detected for user profile {}", userProfileId);
        }

        return UserProfileMapper.toResponse(profile);
    }

    
}
