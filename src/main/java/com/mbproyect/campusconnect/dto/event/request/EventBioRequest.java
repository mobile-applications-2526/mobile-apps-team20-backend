package com.mbproyect.campusconnect.dto.event.request;

import com.mbproyect.campusconnect.model.enums.InterestTag;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventBioRequest {

    private String description;

    private String image;

    @NotNull
    @Size(max = 5)
    private Set<InterestTag> interestTags;

}
