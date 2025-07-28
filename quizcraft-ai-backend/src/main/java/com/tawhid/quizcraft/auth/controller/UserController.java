package com.tawhid.quizcraft.auth.controller;

import com.tawhid.quizcraft.auth.dto.UpdateProfileRequest;
import com.tawhid.quizcraft.auth.dto.UpdateRoleRequest;
import com.tawhid.quizcraft.auth.entity.User;
import com.tawhid.quizcraft.auth.enums.RoleType;
import com.tawhid.quizcraft.auth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Admin - User Management", description = "Admin-only user management endpoints")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "List all users (Admin only)")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "Get user roles by id")
    public ResponseEntity<List<String>> getUserRoles(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserRoles(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile")
    public ResponseEntity<?>updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProfileRequest request
            ){
        return ResponseEntity.ok(userService.updateUserProfile(id, request));
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Update user role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateRoleRequest request
    ){
        userService.assignRole(id,request.getRole());
        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete user")
    public ResponseEntity<?> softDeleteUser(@PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok().build();
    }

}
