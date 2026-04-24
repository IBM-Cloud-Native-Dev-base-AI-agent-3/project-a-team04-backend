package com.himedia.project_a_team04_backend.entity.user;

import com.himedia.project_a_team04_backend.entity.auth.EmailVerificationEntity;
import com.himedia.project_a_team04_backend.entity.auth.PasswordResetEntity;
import com.himedia.project_a_team04_backend.entity.auth.RefreshTokenEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumAttendeeEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumRegistrationEntity;
import com.himedia.project_a_team04_backend.entity.post.PostEntity;
import com.himedia.project_a_team04_backend.entity.post.PostViewEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // CASCADE: 유저 삭제 시 이메일 인증 이력 함께 삭제
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<EmailVerificationEntity> emailVerifications = new ArrayList<>();

    // CASCADE: 유저 삭제 시 비밀번호 재설정 이력 함께 삭제
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PasswordResetEntity> passwordResets = new ArrayList<>();

    // CASCADE: 유저 삭제 시 리프레시 토큰 함께 삭제
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<RefreshTokenEntity> refreshTokens = new ArrayList<>();

    // CASCADE: 유저 삭제 시 조회 기록 함께 삭제
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostViewEntity> postViews = new ArrayList<>();

    // CASCADE: 유저 삭제 시 포럼 신청 이력 함께 삭제
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ForumRegistrationEntity> forumRegistrations = new ArrayList<>();

    // CASCADE: 유저 삭제 시 포럼 참석 정보 함께 삭제
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ForumAttendeeEntity> forumAttendees = new ArrayList<>();

    // RESTRICT: 게시글이 있는 유저는 삭제 불가
    @OneToMany(mappedBy = "user")
    private List<PostEntity> posts = new ArrayList<>();

    // RESTRICT: 개설한 포럼이 있는 유저는 삭제 불가
    @OneToMany(mappedBy = "createdByUser")
    private List<ForumEntity> forums = new ArrayList<>();

    // 탈퇴 이력 (기록 보존용)
    @OneToMany(mappedBy = "user")
    private List<UserWithdrawalEntity> withdrawals = new ArrayList<>();

    @Builder
    public UserEntity(String email, String password, String nickname, String profileImageUrl, UserRole role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.role = role;
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null && !nickname.isBlank()) this.nickname = nickname;
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
    }
}
