package com.mbproyect.campusconnect.service.chat;

import com.mbproyect.campusconnect.dto.chat.request.ChatMessageRequest;
import com.mbproyect.campusconnect.dto.chat.response.ChatMessageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
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

    Page<ChatMessageResponse> getMessages(
            @NotNull UUID chatId,
            @Min(0) int page,
            @Min(1) @Max(50) int size
    );

}
