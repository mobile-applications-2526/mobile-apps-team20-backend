package com.mbproyect.campusconnect.serviceimpl.chat;

import com.mbproyect.campusconnect.config.exceptions.chat.ChatNotFoundException;
import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import com.mbproyect.campusconnect.dto.chat.request.ChatMessageRequest;
import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.infrastructure.mappers.chat.ChatMessageMapper;
import com.mbproyect.campusconnect.infrastructure.repository.chat.ChatMessageRepository;
import com.mbproyect.campusconnect.infrastructure.repository.chat.ChatRepository;
import com.mbproyect.campusconnect.infrastructure.repository.user.UserProfileRepository;
import com.mbproyect.campusconnect.infrastructure.repository.user.UserRepository;
import com.mbproyect.campusconnect.model.entity.chat.ChatMessage;
import com.mbproyect.campusconnect.model.entity.chat.EventChat;
import com.mbproyect.campusconnect.model.entity.user.User;
import com.mbproyect.campusconnect.model.entity.user.UserProfile;
import com.mbproyect.campusconnect.service.chat.ChatMessageService;
import com.mbproyect.campusconnect.service.event.EventService;
import com.mbproyect.campusconnect.service.user.UserService;
import com.mbproyect.campusconnect.shared.util.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service("eventChatMessageService")
public class EventChatMessageService implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRepository chatRepository;
    private final EncryptionUtil encryptionUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final EventService eventService;

    public EventChatMessageService (
            ChatMessageRepository chatMessageRepository,
            ChatRepository chatRepository,
            EncryptionUtil encryptionUtil,
            UserService userService,
            UserRepository userRepository,
            EventService eventService) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRepository = chatRepository;
        this.encryptionUtil = encryptionUtil;
        this.userService = userService;
        this.userRepository = userRepository;
        this.eventService = eventService;
    }

    private boolean isUserAuthorized (String email, UUID eventId) {
        return eventService.doesUserBelongsToEvent(email, eventId);
    }

    private User validateUser(UUID eventId) {

        String currentUserEmail = userService.getCurrentUser();

        if (!isUserAuthorized(currentUserEmail, eventId)) {
            log.warn("User not authorized");
            throw new IllegalStateException("Unauthorized action");
        }

        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("Invalid userprofile id"));
    }

    // Version for websocket connection
    private User validateUser(UUID eventId, String currentUserEmail) {

        if (!isUserAuthorized(currentUserEmail, eventId)) {
            log.warn("User not authorized for websocket connection");
            throw new IllegalStateException("Unauthorized action");
        }

        return userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new UserNotFoundException("Invalid userprofile id"));
    }

    @Transactional
    @Override
    public ChatMessageResponse sendMessage(
            ChatMessageRequest chatMessageRequest,
            UUID chatId,
            String userEmail
    ) {

        log.info("Sending chat message");
        EventChat eventChat = chatRepository.findEventChatById(chatId);

        if (eventChat == null) {
            log.warn("Event chat not found");
            throw new ChatNotFoundException("The id does not match with any chat");
        }

        var eventId = eventChat.getEvent().getEventId();

        User sender = validateUser(eventId, userEmail);
        // Use helper to encrypt message content in the db
        String encryptedContent = encryptionUtil.encrypt(chatMessageRequest.getContent());

        ChatMessage message = ChatMessageMapper
                .toEntity(eventChat, sender.getUserProfile(), encryptedContent);

        // Create the response
        var savedMessage = chatMessageRepository.save(message);
        String decryptedContent = encryptionUtil
                .decrypt(savedMessage.getEncryptedText());

        return ChatMessageMapper
                .toResponse(
                        savedMessage,
                        decryptedContent,
                        sender.getUserProfile().getId()
                );
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ChatMessageResponse> getMessages(
            UUID chatId,
            int page,
            int size
    ) {

        EventChat eventChat = chatRepository.findEventChatById(chatId);

        if (eventChat == null) {
            throw new ChatNotFoundException("The id does not match with any chat");
        }

        // Validate if user belongs to event
        User user = validateUser(eventChat.getEvent().getEventId());

        var sort = Sort.by(Sort.Direction.DESC, "sentAt");
        var pageable = PageRequest.of(page, size, sort);

        // Fetch messages
        Page<ChatMessage> messagesPage = chatMessageRepository.findByChat_Id(chatId, pageable);

        // Reverse the content
        // This ensures they render correctly from top to bottom.
        List<ChatMessage> chronologicalMessages = new ArrayList<>(messagesPage.getContent());
        Collections.reverse(chronologicalMessages);

        // Map to response
        List<ChatMessageResponse> responseList = chronologicalMessages.stream()
                .map(message -> {
                    String decryptedContent = encryptionUtil.decrypt(message.getEncryptedText());
                    return ChatMessageMapper.toResponse(message, decryptedContent, user.getUserProfile().getId());
                })
                .collect(Collectors.toList());

        // Return new PageImpl to preserve pagination metadata
        return new PageImpl<>(responseList, pageable, messagesPage.getTotalElements());
    }

}
