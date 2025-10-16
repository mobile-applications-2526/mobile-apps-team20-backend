package com.mbproyect.campusconnect.model.entity.user;

import com.mbproyect.campusconnect.model.enums.InterestTag;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String userName;

    private int age;

    private String nationality;

    @ElementCollection
    @CollectionTable(
            name = "user_languages",
            joinColumns = @JoinColumn(name = "user_profile_id")
    )
    @Column(name = "language")
    private Set<String> languages;

    @ElementCollection(targetClass = InterestTag.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_bio_tags", joinColumns = @JoinColumn(name = "user_bio_id"))
    @Column(name = "tag")
    private Set<InterestTag> interests;

    @Embedded
    private UserLocation userLocation;

}
