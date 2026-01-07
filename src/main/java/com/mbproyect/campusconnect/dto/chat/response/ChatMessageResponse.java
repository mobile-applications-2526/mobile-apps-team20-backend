package com.mbproyect.campusconnect.dto.chat.response;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String senderProfilePicture;
    private String content;
    private UUID chatId;
    private LocalDateTime sentAt;

    @JsonProperty("isMine")
    private boolean mine;
}
