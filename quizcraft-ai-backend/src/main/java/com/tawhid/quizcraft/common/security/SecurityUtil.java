package com.tawhid.quizcraft.common.security;

import com.tawhid.quizcraft.auth.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtil {
    
    public static User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        
        if (principal instanceof UserDetails) {
            return (User) principal;
        }
        
        throw new RuntimeException("No authenticated user found");
    }
}
