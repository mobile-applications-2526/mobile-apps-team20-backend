package com.mbproyect.campusconnect.model.entity.event;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class EventLocation {

    private String city;

    private String placeName;

}
