package com.mbproyect.campusconnect.model.entity.event;

import com.mbproyect.campusconnect.model.enums.EventStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
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

