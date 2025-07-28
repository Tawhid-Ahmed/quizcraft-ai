package com.tawhid.quizcraft.auth.service;

import com.tawhid.quizcraft.auth.dto.*;
import com.tawhid.quizcraft.auth.entity.PasswordResetToken;
import com.tawhid.quizcraft.auth.entity.Role;
import com.tawhid.quizcraft.auth.entity.User;
import com.tawhid.quizcraft.auth.enums.RoleType;
import com.tawhid.quizcraft.auth.repository.PasswordResetTokenRepository;
import com.tawhid.quizcraft.auth.repository.RoleRepository;
import com.tawhid.quizcraft.auth.repository.UserRepository;
import com.tawhid.quizcraft.common.exceptions.DuplicateEmailException;
import com.tawhid.quizcraft.common.service.JwtService;
import com.tawhid.quizcraft.common.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepo;
    private final MailService mailService;

    private final PasswordResetTokenRepository tokenRepository;

    public AuthResponse register (RegisterRequest req){
        if (userRepository.existsByEmail(req.getEmail()))
            throw new DuplicateEmailException("Email already in use");

        Role studentRole = roleRepo.findByName(RoleType.STUDENT)
                .orElseThrow(() -> new RuntimeException("Default role STUDENT not found"));

        User user = User.builder()
                .name(req.getName())
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .roles(Set.of(studentRole))
                .enabled(true)
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        List<String> roles = user.getRoles().stream().map(r->r.getName().name())
                .toList();

        return new AuthResponse(token,user.getEmail(),user.getName(),roles);
    }

    public AuthResponse login (LoginRequest req){
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));
        if(!encoder.matches(req.getPassword(),user.getPassword()))
            throw new RuntimeException("Invalid credentials");


        String token = jwtService.generateToken(user);
        List<String> roles = user.getRoles().stream()
                .map(r -> r.getName().name())
                .toList();
        return new AuthResponse(token, user.getEmail(), user.getName(), roles);
    }


    public void sendResetToken (String email){
        userRepository.findByEmail(email).ifPresent(user->{
            long tokenLong = System.currentTimeMillis() + (long)(Math.random() * 1000000);
            String token = Long.toString(tokenLong);

            PasswordResetToken passwordResetToken = new PasswordResetToken();
            passwordResetToken.setToken(token);
            passwordResetToken.setUser(user);
            passwordResetToken.setExpiresAt(OffsetDateTime.now(ZoneOffset.UTC).plusMinutes(15));

            tokenRepository.save(passwordResetToken);
            mailService.sendMail(email, "Password Reset",
                    "Click here to reset: http://localhost:5173/reset?token=" + token);

        });
    }
    public void resetPassword (ResetPasswordRequest request){
        PasswordResetToken passwordResetToken = tokenRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if(passwordResetToken.isUsed() || passwordResetToken.getExpiresAt().isBefore(OffsetDateTime.now(ZoneOffset.UTC)))
            throw new RuntimeException("Token expired or already used");

        User user = passwordResetToken.getUser();
        user.setPassword(encoder.encode(request.getPassword()));
        userRepository.save(user);
        passwordResetToken.setUsed(true);
        tokenRepository.save(passwordResetToken);
    }

    public User getProfile (String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid email"));
    }

    public User updateProfile (String email,UpdateProfileRequest req){
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Invalid email"));

        if(req.getName()!=null){ user.setName(req.getName());}
        if(req.getProfileImageUrl()!=null){ user.setProfileImageUrl(req.getProfileImageUrl());}
        return userRepository.save(user);

    }
}
