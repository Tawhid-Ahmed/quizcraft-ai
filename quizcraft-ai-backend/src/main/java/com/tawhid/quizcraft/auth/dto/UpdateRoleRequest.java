package com.tawhid.quizcraft.auth.dto;

import com.tawhid.quizcraft.auth.enums.RoleType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateRoleRequest {

    @NotNull
    private RoleType role;
}
