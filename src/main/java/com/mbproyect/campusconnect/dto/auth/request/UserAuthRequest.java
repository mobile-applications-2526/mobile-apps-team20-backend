package com.mbproyect.campusconnect.dto.auth.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAuthRequest {

    @Email
    @NotNull
    private String email;

}
