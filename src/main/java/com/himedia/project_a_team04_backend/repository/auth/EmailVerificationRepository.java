package com.himedia.project_a_team04_backend.repository.auth;

import com.himedia.project_a_team04_backend.entity.auth.EmailVerificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerificationEntity, Long> {
    Optional<EmailVerificationEntity> findByTokenHash(String tokenHash);
}
