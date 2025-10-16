package com.mbproyect.campusconnect.model.entity.event;

import com.mbproyect.campusconnect.model.entity.user.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EventOrganiser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    private UserProfile userProfile;

    @OneToMany(mappedBy = "organiser", fetch = FetchType.EAGER)
    private Set<Event> events;
}
