package com.mbproyect.campusconnect.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventChatResponse {

    private UUID id;

    private UUID eventId;

    private ChatMessageResponse lastMessage;

    private String eventImage;
    
}
