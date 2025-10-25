package com.mbproyect.campusconnect.serviceimpl.chat;

import com.mbproyect.campusconnect.config.exceptions.chat.ChatNotFoundException;
import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import com.mbproyect.campusconnect.dto.chat.request.ChatMessageRequest;
import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.infrastructure.mappers.chat.ChatMessageMapper;
import com.mbproyect.campusconnect.infrastructure.repository.chat.ChatMessageRepository;
import com.mbproyect.campusconnect.infrastructure.repository.chat.ChatRepository;
import com.mbproyect.campusconnect.infrastructure.repository.user.UserProfileRepository;
import com.mbproyect.campusconnect.model.entity.chat.ChatMessage;
import com.mbproyect.campusconnect.model.entity.chat.EventChat;
import com.mbproyect.campusconnect.model.entity.user.UserProfile;
import com.mbproyect.campusconnect.service.chat.ChatMessageService;
import com.mbproyect.campusconnect.shared.util.EncryptionUtil;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("eventChatMessageService")
public class EventChatMessageService implements ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRepository chatRepository;
    private final UserProfileRepository userProfileRepository;
    private final EncryptionUtil encryptionUtil;

    public EventChatMessageService (
            ChatMessageRepository chatMessageRepository,
            ChatRepository chatRepository,
            UserProfileRepository userRepository,
            EncryptionUtil encryptionUtil
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRepository = chatRepository;
        this.userProfileRepository = userRepository;
        this.encryptionUtil = encryptionUtil;
    }

    @Override
    public ChatMessageResponse sendMessage(ChatMessageRequest chatMessageRequest, UUID chatId) {
        EventChat eventChat = chatRepository.findEventChatById(chatId);

        if (eventChat == null) {
            throw new ChatNotFoundException("The id does not match with any chat");
        }

        //TODO: When server authenticated, take userprofile by the actual user of the token
        UserProfile sender = userProfileRepository.findById(chatMessageRequest.getUserProfileId())
                .orElseThrow(() -> new UserNotFoundException("Invalid userprofile id"));

        // Use helper to encrypt message content in the db
        String encryptedContent = encryptionUtil.encrypt(chatMessageRequest.getContent());

        ChatMessage message = ChatMessageMapper
                .toEntity(eventChat, sender, encryptedContent);

        chatMessageRepository.save(message);

        return this.chatMessageRepository
                .findChatMessageById(message.getId());
    }

}
