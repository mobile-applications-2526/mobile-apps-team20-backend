package com.mbproyect.campusconnect.service.storage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
public interface StorageService {

    void saveValue(@NotNull String key, @NotBlank String value);

    boolean isKeySaved(@NotNull String key);

    String getValue(@NotNull String key);
}