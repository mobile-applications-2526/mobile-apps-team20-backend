package com.mbproyect.campusconnect.serviceimpl.chat;

import com.mbproyect.campusconnect.config.exceptions.chat.ChatNotFoundException;
import com.mbproyect.campusconnect.config.exceptions.event.UserNotFoundException;
import com.mbproyect.campusconnect.dto.chat.request.ChatMessageRequest;
import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.infrastructure.mappers.chat.ChatMessageMapper;
import com.mbproyect.campusconnect.infrastructure.repository.chat.ChatMessageRepository;
import com.mbproyect.campusconnect.infrastructure.repository.chat.ChatRepository;
import com.mbproyect.campusconnect.infrastructure.repository.user.UserRepository;
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
    private final UserRepository userRepository;

    public EventChatMessageService (
            ChatMessageRepository chatMessageRepository,
            ChatRepository chatRepository,
            UserRepository userRepository
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ChatMessageResponse sendMessage(ChatMessageRequest chatMessageRequest, UUID chatId) {
        EventChat eventChat = chatRepository.findEventChatById(chatId);

        if (eventChat == null) {
            throw new ChatNotFoundException("The id does not match with any chat");
        }

        //TODO: When server authenticated, take userprofile by the actual user of the token
        UserProfile sender = userRepository
                .findUserByUserProfile_Id(chatMessageRequest.getUserProfileId());

        if (sender == null) {
            throw new UserNotFoundException("Invalid userprofile id");
        }

        // Use helper to encrypt message content in the db
        String encryptedContent = EncryptionUtil.encrypt(chatMessageRequest.getContent());

        ChatMessage message = ChatMessageMapper
                .toEntity(eventChat, sender, encryptedContent);

        chatMessageRepository.save(message);

        return this.chatMessageRepository
                .findChatMessageById(message.getId());
    }

}
