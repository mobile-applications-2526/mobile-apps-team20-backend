package com.mbproyect.campusconnect.serviceimpl.chat;

import com.mbproyect.campusconnect.dto.chat.request.ChatMessageRequest;
import com.mbproyect.campusconnect.dto.chat.request.MarkChatReadRequest;
import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import com.mbproyect.campusconnect.service.chat.ChatMessageService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service("privateChatMessageService")
public class PrivateChatMessageService implements ChatMessageService {

    @Override
    public ChatMessageResponse sendMessage(
            ChatMessageRequest chatMessageRequest,
            UUID chatId,
            String userEmail
    ) {
        return null; // TODO
    }

    @Override
    public Page<ChatMessageResponse> getMessages(
            UUID chatId,
            int page,
            int size
    ) {
        return null; // TODO
    }

    @Override
    public void markRead(UUID chatId, MarkChatReadRequest request) {
        //TODO
    }

}
