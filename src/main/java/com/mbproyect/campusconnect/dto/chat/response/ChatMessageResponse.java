package com.mbproyect.campusconnect.dto.chat.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageResponse {

    private UUID id;
    private UUID senderId;
    private String senderName;
    private byte[] senderProfilePicture;
    private String content;
    private LocalDateTime sentAt;

}
