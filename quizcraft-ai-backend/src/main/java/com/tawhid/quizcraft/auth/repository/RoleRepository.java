package com.tawhid.quizcraft.auth.repository;

import com.tawhid.quizcraft.auth.entity.Role;
import com.tawhid.quizcraft.auth.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName (RoleType name);
}
