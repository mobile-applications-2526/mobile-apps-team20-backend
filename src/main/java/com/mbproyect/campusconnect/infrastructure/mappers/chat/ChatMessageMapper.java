package com.mbproyect.campusconnect.infrastructure.mappers.chat;

import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.model.entity.chat.ChatMessage;
import com.mbproyect.campusconnect.model.entity.chat.EventChat;
import com.mbproyect.campusconnect.model.entity.user.UserProfile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChatMessageMapper {

    private ChatMessageMapper() {}

    public static ChatMessage toEntity(EventChat chat, UserProfile sender, String encryptedText) {
        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setSender(sender);
        message.setEncryptedText(encryptedText);
        return message;
    }


    static public ChatMessageResponse toResponse(
            ChatMessage entity,
            String decryptedText,
            UUID actualUserId
    ) {
        // Now profilePicture is a String filename/path, just pass it through
        String senderImage = entity.getSender().getProfilePicture();

        return new ChatMessageResponse(
                entity.getId(),
                entity.getSender().getId(),
                entity.getSender().getUserName(),
                senderImage,
                decryptedText,
                entity.getChat().getId(),
                entity.getSentAt(),
                actualUserId.equals(entity.getSender().getId())
        );
    }
}

