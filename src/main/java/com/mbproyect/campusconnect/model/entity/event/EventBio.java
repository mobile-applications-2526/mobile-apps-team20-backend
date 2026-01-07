package com.mbproyect.campusconnect.model.entity.event;

import com.mbproyect.campusconnect.model.enums.InterestTag;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class EventBio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private UUID id;

    private String description;

    private String imageUrl; // O multipartFile

    /**
     * Creates a table for the enum tag
     */
    @ElementCollection(targetClass = InterestTag.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "event_bio_tags", joinColumns = @JoinColumn(name = "event_bio_id"))
    @Column(name = "tag")
    private Set<InterestTag> interestTags;

    public EventBio(String description, String image, Set<InterestTag> interestTags) {
        this.description = description;
        this.imageUrl = image;
        this.interestTags = interestTags;
    }

}
