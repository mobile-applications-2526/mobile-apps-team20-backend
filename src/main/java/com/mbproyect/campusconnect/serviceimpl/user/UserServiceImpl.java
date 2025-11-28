package com.mbproyect.campusconnect.serviceimpl.user;

import com.mbproyect.campusconnect.config.exceptions.user.UserNotFoundException;
import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.dto.chat.response.EventChatResponse;
import com.mbproyect.campusconnect.infrastructure.mappers.chat.ChatMapper;
import com.mbproyect.campusconnect.infrastructure.repository.chat.ChatRepository;
import com.mbproyect.campusconnect.infrastructure.repository.user.UserRepository;
import com.mbproyect.campusconnect.model.entity.chat.ChatMessage;
import com.mbproyect.campusconnect.model.entity.chat.EventChat;
import com.mbproyect.campusconnect.model.entity.user.User;
import com.mbproyect.campusconnect.model.entity.user.UserProfile;
import com.mbproyect.campusconnect.model.enums.EventStatus;
import com.mbproyect.campusconnect.service.user.UserService;
import com.mbproyect.campusconnect.shared.util.EncryptionUtil;
import org.hibernate.query.NativeQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final EncryptionUtil encryptionUtil;

    public UserServiceImpl(
            UserRepository userRepository,
            ChatRepository chatRepository,
            EncryptionUtil encryptionUtil
    ) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.encryptionUtil = encryptionUtil;
    }

    @Override
    public String getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Access denied: User not authenticated");
        }
        // Principal object must be an instance of UserPrincipal
        if (!(authentication.getPrincipal() instanceof String email)) {
            throw new IllegalStateException("Config error, principal is not UserPrincipal");
        }
        return email;
    }

    @Override
    public void validateCurrentUser(String email) {
        if (!this.getCurrentUser().equals(email)) {
            throw new IllegalStateException(
                    "The current user is not authorized to perform this operation"
            );
        }
    }

    @Override
    public Page<EventChatResponse> getChats(int page, int size) {
        String userEmail = getCurrentUser();

        User actualUser = this.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        var sort = Sort.by(Sort.Direction.DESC, "messages.sentAt");
        var pageable = PageRequest.of(page, size, sort);

        Page<EventChat> chats = chatRepository.getUserChats(
                userEmail,
                EventStatus.ACTIVE,
                pageable
        );

        if (chats.isEmpty()) {
            return Page.empty();
        }

        return chats.map(
                chat -> {
                    // 1. Get the latest message comparing by 'sentAt'
                    ChatMessage lastMessage = chat.getMessages().stream()
                            .max(Comparator.comparing(ChatMessage::getSentAt))
                            .orElse(null);

                    // 2. Decrypt content if message exists
                    String decryptedContent = null;
                    if (lastMessage != null) {
                        decryptedContent = encryptionUtil
                                .decrypt(lastMessage.getEncryptedText());
                    }
                    return ChatMapper
                            .toResponse(
                                    chat,
                                    lastMessage,
                                    decryptedContent,
                                    actualUser.getUserId()
                            );
                }
        );
    }

    @Override
    public User createUser(String email) {
        String initialUsername = email.substring(0, 6);
        var userProfile = new UserProfile();
        userProfile.setUserName(initialUsername);

        var user = new User();
        user.setEmail(email);
        user.setUserProfile(userProfile);
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
