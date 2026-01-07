package com.mbproyect.campusconnect.infrastructure.mappers.event;

import com.mbproyect.campusconnect.dto.event.request.EventParticipantRequest;
import com.mbproyect.campusconnect.dto.event.response.EventParticipantResponse;
import com.mbproyect.campusconnect.dto.user.response.UserProfileResponse;
import com.mbproyect.campusconnect.infrastructure.mappers.user.UserProfileMapper;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.model.entity.event.EventParticipant;
import com.mbproyect.campusconnect.model.entity.user.UserProfile;

public class EventParticipantMapper {

    public static EventParticipantResponse toResponse(EventParticipant participant) {
        if (participant == null) return null;

        UserProfileResponse userProfileResponse =
                UserProfileMapper.toResponse(participant.getUserProfile());
        Event event = participant.getEvent();

        EventParticipantResponse response = new EventParticipantResponse();
        response.setId(participant.getId());
        response.setUserProfile(userProfileResponse);
        response.setEventId(event != null ? event.getEventId() : null);
        response.setEmail(participant.getEmail());

        return response;
    }

    public static EventParticipant fromRequest(EventParticipantRequest request, Event event) {
        if (request == null) return null;

        UserProfile userProfile = UserProfileMapper.fromRequest(request.getUserProfile());

        EventParticipant participant = new EventParticipant();
        participant.setUserProfile(userProfile);
        participant.setEvent(event);

        return participant;
    }
}
