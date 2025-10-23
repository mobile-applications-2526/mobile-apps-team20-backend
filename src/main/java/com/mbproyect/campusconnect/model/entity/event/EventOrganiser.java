package com.mbproyect.campusconnect.model.entity.event;

import com.mbproyect.campusconnect.model.entity.user.UserProfile;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class EventOrganiser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String email;

    @OneToOne
    private UserProfile userProfile;

    @OneToMany(mappedBy = "organiser")
    private Set<Event> events = new HashSet<>();
}
