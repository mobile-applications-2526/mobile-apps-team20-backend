package com.mbproyect.campusconnect.service.chat;

import com.mbproyect.campusconnect.model.entity.chat.EventChat;
import com.mbproyect.campusconnect.model.entity.event.Event;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
@Service
public interface EventChatService {

    EventChat createChat(@Valid Event event);

    long getMessagesCountAfter(UUID messageId);
}
