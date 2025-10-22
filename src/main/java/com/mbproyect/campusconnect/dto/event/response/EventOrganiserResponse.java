package com.mbproyect.campusconnect.dto.event.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventOrganiserResponse {

    private UUID id;

    private String username;
}
