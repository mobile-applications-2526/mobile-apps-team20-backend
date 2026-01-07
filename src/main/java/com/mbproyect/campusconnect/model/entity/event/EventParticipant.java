package com.mbproyect.campusconnect.model.entity.event;

import com.mbproyect.campusconnect.model.entity.user.UserProfile;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false)
    private String email;

    @ManyToOne
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    private UUID lastMessageIdSeen;

}
