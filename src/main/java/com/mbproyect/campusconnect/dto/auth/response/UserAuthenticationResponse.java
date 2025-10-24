package com.mbproyect.campusconnect.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserAuthenticationResponse {

    private String email;

    private String jwt;

    private UUID refreshToken;

}
