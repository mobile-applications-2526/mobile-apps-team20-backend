package com.mbproyect.campusconnect.dto.event.request;

import com.mbproyect.campusconnect.dto.user.request.UserProfileRequest;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventOrganiserRequest {

    @NotNull
    private UserProfileRequest userProfile;

    @NotNull
    private Set<UUID> eventsIds; //TODO Should it be onetoone?
}
