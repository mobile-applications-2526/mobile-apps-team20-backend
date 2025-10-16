package com.mbproyect.campusconnect.model.entity.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    private String email;

    // User operations affect to userprofile
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile userProfile;

    private boolean isActive;

}
