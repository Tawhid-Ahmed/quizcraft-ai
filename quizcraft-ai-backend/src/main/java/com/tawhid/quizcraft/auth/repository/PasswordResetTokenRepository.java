package com.tawhid.quizcraft.auth.repository;

import com.tawhid.quizcraft.auth.entity.PasswordResetToken;
import com.tawhid.quizcraft.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(User user);
}
