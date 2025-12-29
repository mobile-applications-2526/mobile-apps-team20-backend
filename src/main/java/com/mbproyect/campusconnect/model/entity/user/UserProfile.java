package com.mbproyect.campusconnect.model.entity.user;

import com.mbproyect.campusconnect.model.enums.InterestTag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    @Column(nullable = false, unique = true)
    private String userName;

    private int age;

    private String nationality;

    private String bio;

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

    @ElementCollection
    @CollectionTable(name = "user_social_media", joinColumns = @JoinColumn(name = "user_profile_id"))
    @MapKeyColumn(name = "platform_name")
    @Column(name = "platform_username")
    private Map<String, String> socialMedia;

    @Embedded
    private UserLocation userLocation;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @JdbcTypeCode(Types.VARBINARY)
    private byte[] profilePicture;

}
