package com.mbproyect.campusconnect.serviceimpl.chat;

import com.mbproyect.campusconnect.dto.chat.request.ChatMessageRequest;
import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.service.chat.ChatMessageService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("eventChatMessageService")
public class EventChatMessageService implements ChatMessageService {

    @Override
    public ChatMessageResponse sendMessage(ChatMessageRequest chatMessageRequest, UUID chatId) {
        return null; // TODO
    }

}
