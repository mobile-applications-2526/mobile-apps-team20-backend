package com.juangomez.campusconnect.entity.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private UUID eventId;

    private String name;

    private String eventBio;

    private EventOrganiser organiser;

    private String location;

    private List<EventParticipant> participants;

    private Date date;

    private List<EventTag> eventTag;

}

