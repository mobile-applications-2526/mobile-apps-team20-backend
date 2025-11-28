package com.mbproyect.campusconnect.controller.chat.rest;

import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.service.chat.ChatMessageService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("/api/chat")
public class EventChatMessageController {

    private final ChatMessageService chatMessageService;

    public EventChatMessageController(ChatMessageService eventChatMessageService) {
        this.chatMessageService = eventChatMessageService;
    }

    // Get messages from a chat
    @GetMapping("{chatId}")
    public ResponseEntity<Page<ChatMessageResponse>> getChatMessages(
            @PathVariable UUID chatId,
            int page,
            int size
    ) {
        return ResponseEntity.ok(chatMessageService
                .getMessages(chatId, page, size));
    }

}
