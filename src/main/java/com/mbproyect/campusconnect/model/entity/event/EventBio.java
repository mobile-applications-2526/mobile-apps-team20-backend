package com.mbproyect.campusconnect.model.entity.event;

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
public class EventBio {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String description;

    private byte [] image;

    /**
     * Creates a table for the enum tag
     */
    @ElementCollection(targetClass = InterestTag.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "event_bio_tags", joinColumns = @JoinColumn(name = "event_bio_id"))
    @Column(name = "tag")
    private Set<InterestTag> interestTags;

    public EventBio(String description, byte[] image, Set<InterestTag> interestTags) {
        this.description = description;
        this.image = image;
        this.interestTags = interestTags;
    }

}
