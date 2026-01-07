package com.mbproyect.campusconnect.dto.event.response;

import com.mbproyect.campusconnect.model.enums.InterestTag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventBioResponse {

    private UUID id;

    private String description;

    private String image;

    private Set<InterestTag> interestTags;

}
