package com.mbproyect.campusconnect.dto.user.request;

import com.mbproyect.campusconnect.model.entity.user.UserLocation;
import com.mbproyect.campusconnect.model.enums.InterestTag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {

    @NotNull
    @Size(min = 3)
    private String userName;

    @NotNull
    private String nationality;

    private String bio;

    private Map<String, String> socialMedia;

    @NotNull
    private Set<String> languages;

    @NotNull
    private int age;

    @NotNull
    private Set<InterestTag> interests;

    @NotNull
    private UserLocation userLocation;

    // Stores the filename/path of the uploaded profile picture (if any)
    private String profilePicture;

}
