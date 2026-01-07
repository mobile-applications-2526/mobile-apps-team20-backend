package com.mbproyect.campusconnect.controller.chat.websocket;

import com.mbproyect.campusconnect.dto.chat.request.ChatMessageRequest;
import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.dto.event.response.EventParticipantResponse;
import com.mbproyect.campusconnect.service.chat.ChatMessageService;
import com.mbproyect.campusconnect.service.event.EventParticipantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *  Websocket controller for event chats
 */

@Slf4j
@Controller
public class EventChatWebsocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService messageService;
    private final EventParticipantService eventParticipantService;

    public EventChatWebsocketController(
            SimpMessagingTemplate messagingTemplate,
            @Qualifier("eventChatMessageService") ChatMessageService messageService,
            EventParticipantService eventParticipantService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
        this.eventParticipantService = eventParticipantService;
    }

    private void sendEventMessage(
            UUID chatId,
            ChatMessageResponse response
    ) {
        // Sends the message to chatId subscribers
        log.info("Chat message sent");

        messagingTemplate.convertAndSend(
                "/event/chat/" + chatId, response
        );
    }

    private void notifyEventListeners(
            UUID chatId,
            ChatMessageResponse response,
            Principal principal
    ) {
        // Private Queue: Updates the chat list for all participants
        log.info("Notifying chat listeners");

        // Get chat listeners
        Set<String> subscribers = eventParticipantService
                .getParticipantsByEventChatId(chatId).stream()
                .map(EventParticipantResponse::getEmail)
                .collect(Collectors.toSet());

        // Notify the sender avoiding fetch all chats for update
        subscribers.add(principal.getName());

        for (var subscriber : subscribers) {
            // Sends to /user/{username}/queue/chats
            messagingTemplate.convertAndSendToUser(
                    subscriber,
                    "/queue/chats",
                    response
            );
        }
    }

    @MessageMapping("/event/{chatId}/send")
    public void sendMessage(
            @DestinationVariable UUID chatId,
            ChatMessageRequest messageRequest,
            Principal principal  // Provides the authentication state
    ) {
        ChatMessageResponse chatMessageResponse = messageService
                .sendMessage(messageRequest, chatId, principal.getName());

        sendEventMessage(chatId, chatMessageResponse);
        notifyEventListeners(
                chatId,
                chatMessageResponse,
                principal
        );
    }

}
