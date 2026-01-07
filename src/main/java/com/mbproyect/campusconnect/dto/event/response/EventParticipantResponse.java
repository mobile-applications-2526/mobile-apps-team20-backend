package com.mbproyect.campusconnect.dto.event.response;

import com.mbproyect.campusconnect.dto.user.response.UserProfileResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventParticipantResponse {

    private UUID id;

    private String email;

    private UserProfileResponse userProfile;

    private UUID eventId;
}
