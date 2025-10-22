package com.mbproyect.campusconnect.serviceimpl.chat;

import com.mbproyect.campusconnect.config.exceptions.event.EventNotFoundException;
import com.mbproyect.campusconnect.infrastructure.repository.chat.ChatRepository;
import com.mbproyect.campusconnect.model.entity.chat.EventChat;
import com.mbproyect.campusconnect.model.entity.event.Event;
import com.mbproyect.campusconnect.service.chat.EventChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventChatServiceImpl implements EventChatService {

    private final ChatRepository chatRepository;

    public EventChatServiceImpl(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Override
    public void createChat(Event event) {
        if (event == null) {
            throw new EventNotFoundException("Event cannot be null");
        }

        EventChat chat = new EventChat();
        chat.setEvent(event);

        chatRepository.save(chat);
        log.info("Chat with id {} created", chat.getId());
    }
}
