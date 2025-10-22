package com.mbproyect.campusconnect.infrastructure.mappers.chat;

import com.mbproyect.campusconnect.dto.chat.response.EventChatResponse;
import com.mbproyect.campusconnect.model.entity.chat.EventChat;

public class ChatMapper {

    public static EventChatResponse toResponse (EventChat chat) {
        if (chat == null) {
            throw new IllegalArgumentException("Chat cannot be null");
        }
        return new EventChatResponse(
                chat.getId(), chat.getMessages().size()
        );
    }
}
