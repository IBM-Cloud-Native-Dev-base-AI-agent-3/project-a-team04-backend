package com.himedia.project_a_team04_backend.repository.user;

import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByIdAndIsDeletedFalse(Long id);
    boolean existsByEmail(String email);
}
