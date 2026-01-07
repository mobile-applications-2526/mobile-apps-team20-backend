package com.mbproyect.campusconnect.controller.chat.websocket;

import com.mbproyect.campusconnect.dto.chat.request.ChatMessageRequest;
import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.service.chat.ChatMessageService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

/**
 *  Websocket controller for private chats
 */

@Controller
public class PrivateChatWebsocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService messageService;

    public PrivateChatWebsocketController(
            SimpMessagingTemplate messagingTemplate,
            @Qualifier("privateChatMessageService") ChatMessageService messageService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/user/{chatId}/send")
    public void sendMessage(
            @DestinationVariable UUID chatId,
            ChatMessageRequest messageRequest,
            Principal principal  // Provides the authentication state
    ) {
        ChatMessageResponse chatMessageResponse = messageService
                .sendMessage(messageRequest, chatId, principal.getName());

        // Sends the message to chatId subscribers
        messagingTemplate.convertAndSend(
                "/user/chat/" + chatId, chatMessageResponse
        );
    }
}
