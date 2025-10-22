package com.mbproyect.campusconnect.service.chat;

import com.mbproyect.campusconnect.dto.chat.request.ChatMessageRequest;
import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Validated
@Service
public interface ChatMessageService {

    ChatMessageResponse sendMessage(
            @Valid ChatMessageRequest chatMessageRequest,
            @NotNull UUID chatId
    );

}
