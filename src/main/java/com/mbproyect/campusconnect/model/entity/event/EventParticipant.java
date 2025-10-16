package com.mbproyect.campusconnect.model.entity.event;

import com.mbproyect.campusconnect.model.entity.user.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EventParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private UserProfile userProfile;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

}
