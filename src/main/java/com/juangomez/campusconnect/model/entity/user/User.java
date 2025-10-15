package com.juangomez.campusconnect.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class User {

    private UUID userId;

    private UserProfile userProfile;

    private boolean isActive;


}
