package com.mbproyect.campusconnect.controller.chat.rest;

import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.serviceimpl.chat.EventChatMessageService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("/api/chat")
public class EventChatMessageController {

    private final EventChatMessageService eventChatMessageService;

    public EventChatMessageController(EventChatMessageService eventChatMessageService) {
        this.eventChatMessageService = eventChatMessageService;
    }

    // Get messages from a chat
    @GetMapping("{chatId}")
    public ResponseEntity<Page<ChatMessageResponse>> getChatMessages(
            @PathVariable UUID chatId,
            int page,
            int size
    ) {
        return ResponseEntity.ok(eventChatMessageService
                .getMessages(chatId, page, size));
    }

}
