package com.mbproyect.campusconnect.model.entity.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID userId;

    @Column(nullable = false, unique = true)
    private String email;

    // User operations affect to userprofile
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile userProfile;

    private boolean isActive;

}
