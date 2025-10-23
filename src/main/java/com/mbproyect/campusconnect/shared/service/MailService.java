package com.mbproyect.campusconnect.shared.service;

import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Validated
@Service
public interface MailService {

    void sendEmail(@NotBlank String sendTo, @NotBlank String subject, @NotBlank String body);

}
