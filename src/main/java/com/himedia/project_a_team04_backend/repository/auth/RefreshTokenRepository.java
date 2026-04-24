package com.himedia.project_a_team04_backend.repository.auth;

import com.himedia.project_a_team04_backend.entity.auth.RefreshTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);
    void deleteByUser_Id(Long userId);
}
