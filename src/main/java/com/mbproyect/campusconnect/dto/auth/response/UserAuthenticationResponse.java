package com.mbproyect.campusconnect.dto.auth.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserAuthenticationResponse {

    private String email;

    private String username;

    private String accessToken;

    private UUID refreshToken;

}
