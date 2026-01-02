package com.mbproyect.campusconnect.infrastructure.mappers.chat;

import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.dto.chat.response.EventChatResponse;
import com.mbproyect.campusconnect.model.entity.chat.ChatMessage;
import com.mbproyect.campusconnect.model.entity.chat.EventChat;

import java.util.Base64;
import java.util.UUID;

public class ChatMapper {

    private ChatMapper() {
        // Prevent instantiation
    }

    public static EventChatResponse toResponse(
            EventChat chat,
            ChatMessage lastMessage,
            String decryptedContent,
            UUID currentUserId
    ) {
        if (chat == null) {
            throw new IllegalArgumentException("Chat cannot be null");
        }

        String eventImage = null;
        // Null checks before accessing the byte array
        if (chat.getEvent() != null
                && chat.getEvent().getEventBio() != null
                && chat.getEvent().getEventBio().getImageUrl() != null) {
            eventImage = chat.getEvent().getEventBio().getImageUrl();
        }

        ChatMessageResponse lastMessageResponse = null;
        if (lastMessage != null) {
            lastMessageResponse = ChatMessageMapper.toResponse(lastMessage, decryptedContent, currentUserId);
        }

        return new EventChatResponse(
                chat.getId(),
                chat.getEvent() != null ? chat.getEvent().getEventId() : null,
                chat.getEvent() != null ? chat.getEvent().getName() : null,
                lastMessageResponse,
                eventImage
        );
    }
}