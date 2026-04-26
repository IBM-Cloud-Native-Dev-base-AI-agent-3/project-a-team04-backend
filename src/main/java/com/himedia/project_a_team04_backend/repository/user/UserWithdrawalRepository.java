package com.himedia.project_a_team04_backend.repository.user;

import com.himedia.project_a_team04_backend.entity.user.UserWithdrawalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserWithdrawalRepository extends JpaRepository<UserWithdrawalEntity, Long> {
    Optional<UserWithdrawalEntity> findTopByEmailOrderByWithdrawnAtDesc(String email);
}
