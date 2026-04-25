package com.himedia.project_a_team04_backend.repository.auth;

import com.himedia.project_a_team04_backend.entity.auth.PasswordResetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordResetEntity, Long> {
    Optional<PasswordResetEntity> findByTokenHash(String tokenHash);
}
