package com.mbproyect.campusconnect.service.chat;

import com.mbproyect.campusconnect.model.entity.event.Event;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public interface EventChatService {

    void createChat(@Valid Event event);

}
