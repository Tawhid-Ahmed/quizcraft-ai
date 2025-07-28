package com.tawhid.quizcraft.auth.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String profileImageUrl;
}
