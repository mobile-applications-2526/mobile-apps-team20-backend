package com.mbproyect.campusconnect.model.entity.event;

import com.mbproyect.campusconnect.model.entity.chat.EventChat;
import com.mbproyect.campusconnect.model.enums.EventStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(exclude = "chat")
@ToString(exclude = "chat")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID eventId;

    private String name;

    @OneToOne()
    @JoinColumn(name = "event_bio_id")
    private EventBio eventBio;

    @ManyToOne
    private EventOrganiser organiser;

    @Embedded
    private EventLocation location;

    @OneToOne(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private EventChat chat;

    /**
     * Foreign key already exists in participant table
     * mappedBy is used to avoid creating other table with event & participants id's
     */
    @OneToMany(mappedBy = "event")
    private Set<EventParticipant> participants;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

}

