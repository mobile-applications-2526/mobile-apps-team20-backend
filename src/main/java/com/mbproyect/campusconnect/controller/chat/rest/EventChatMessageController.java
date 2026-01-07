package com.mbproyect.campusconnect.controller.chat.rest;

import com.mbproyect.campusconnect.dto.chat.request.MarkChatReadRequest;
import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.service.chat.ChatMessageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/chat")
public class EventChatMessageController {

    private final ChatMessageService chatMessageService;

    public EventChatMessageController(ChatMessageService eventChatMessageService) {
        this.chatMessageService = eventChatMessageService;
    }

    // Get messages from a chat
    @GetMapping("{chatId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getChatMessages(
            @PathVariable UUID chatId,
            int page,
            int size
    ) {
        return ResponseEntity.ok(chatMessageService
                .getMessages(chatId, page, size));
    }

    @PostMapping("{chatId}/mark-read")
    public ResponseEntity<Void> markRead (
            @PathVariable UUID chatId,
            @RequestBody @Valid MarkChatReadRequest request
    ) {
        chatMessageService.markRead(chatId, request);
        return ResponseEntity.noContent().build();
    }

}
