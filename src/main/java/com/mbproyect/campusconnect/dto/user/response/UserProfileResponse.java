package com.mbproyect.campusconnect.dto.user.response;

import com.mbproyect.campusconnect.model.entity.user.UserLocation;
import com.mbproyect.campusconnect.model.enums.InterestTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {

    private UUID id;

    private String userName;

    private String bio;

    private Map<String, String> socialMedia;

    private String nationality;

    private Set<String> languages;

    private int age;

    private Set<InterestTag> interests;

    private UserLocation userLocation;

    private byte[] profilePicture;
}
