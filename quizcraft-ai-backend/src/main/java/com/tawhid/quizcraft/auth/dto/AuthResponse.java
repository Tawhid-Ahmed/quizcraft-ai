package com.tawhid.quizcraft.auth.dto;

import com.tawhid.quizcraft.auth.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String name;
    private List<String> role;
}
