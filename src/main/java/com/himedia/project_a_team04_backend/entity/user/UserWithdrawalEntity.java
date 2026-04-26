package com.himedia.project_a_team04_backend.entity.user;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_withdrawals")
@Getter
@NoArgsConstructor
public class UserWithdrawalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable: 탈퇴 이력은 유저 삭제 후에도 기록 보존
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "withdrawn_at", nullable = false)
    private LocalDateTime withdrawnAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public UserWithdrawalEntity(UserEntity user, String email, String reason, LocalDateTime withdrawnAt) {
        this.user = user;
        this.email = email;
        this.reason = reason;
        this.withdrawnAt = withdrawnAt;
    }
}
