package com.mbproyect.campusconnect.infrastructure.mappers.user;

import com.mbproyect.campusconnect.dto.user.request.UserProfileRequest;
import com.mbproyect.campusconnect.dto.user.response.UserProfileResponse;
import com.mbproyect.campusconnect.model.entity.user.UserProfile;
import com.mbproyect.campusconnect.model.entity.user.UserLocation;

import java.util.HashSet;
import java.util.Set;

public class UserProfileMapper {

    /**
     * Converts a UserProfile entity into a UserProfileResponse DTO.
     */
    public static UserProfileResponse toResponse(UserProfile userProfile) {
        if (userProfile == null) {
            return null;
        }

        UserProfileResponse response = new UserProfileResponse();
        response.setUserName(userProfile.getUserName());
        response.setNationality(userProfile.getNationality());
        response.setAge(userProfile.getAge());
        response.setLanguages(
                userProfile.getLanguages() != null ? new HashSet<>(userProfile.getLanguages()) : Set.of()
        );
        response.setInterests(
                userProfile.getInterests() != null ? new HashSet<>(userProfile.getInterests()) : Set.of()
        );
        response.setUserLocation(copyLocation(userProfile.getUserLocation()));
        response.setId(userProfile.getId());
    response.setProfilePicture(userProfile.getProfilePicture());

        return response;
    }

    /**
     * Converts a UserProfileRequest DTO into a UserProfile entity.
     */
    public static UserProfile fromRequest(UserProfileRequest request) {
        if (request == null) {
            return null;
        }

        UserProfile userProfile = new UserProfile();
        userProfile.setUserName(request.getUserName());
        userProfile.setNationality(request.getNationality());
        userProfile.setAge(request.getAge());
        userProfile.setLanguages(
                request.getLanguages() != null ? new HashSet<>(request.getLanguages()) : new HashSet<>()
        );
        userProfile.setInterests(
                request.getInterests() != null ? new HashSet<>(request.getInterests()) : new HashSet<>()
        );
        userProfile.setUserLocation(copyLocation(request.getUserLocation()));
        userProfile.setProfilePicture(request.getProfilePicture());

        return userProfile;
    }


    /**
     * Copies the nested UserLocation object safely.
     */
    private static UserLocation copyLocation(UserLocation source) {
        if (source == null) {
            return null;
        }
        UserLocation location = new UserLocation();
        location.setCity(source.getCity());
        location.setCountry(source.getCountry());
        return location;
    }
}

