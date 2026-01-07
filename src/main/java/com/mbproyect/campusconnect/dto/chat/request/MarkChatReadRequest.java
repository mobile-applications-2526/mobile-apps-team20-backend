package com.mbproyect.campusconnect.dto.chat.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record MarkChatReadRequest (
        @NotNull UUID messageId
)
{}
