package com.tawhid.quizcraft.auth.repository;

import com.tawhid.quizcraft.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndDeletedAtIsNull(String email);
    Optional<User> findByIdAndDeletedAtIsNull(Long id);
    List<User> findAllByDeletedAtIsNull();
    boolean existsByEmail(String email);

}
