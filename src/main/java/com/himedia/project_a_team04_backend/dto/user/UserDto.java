package com.himedia.project_a_team04_backend.dto.user;

import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.entity.user.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserDto {

    @Getter
    @NoArgsConstructor
    public static class SignupRequest {
        private String email;
        private String password;
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateProfileRequest {
        private String nickname;
        private String profileImageUrl;
    }

    @Getter
    @NoArgsConstructor
    public static class WithdrawalRequest {
        private String reason;
    }

    @Getter
    public static class ProfileResponse {
        private final Long id;
        private final String email;
        private final String nickname;
        private final String profileImageUrl;
        private final UserRole role;
        private final boolean isActive;
        private final boolean emailVerified;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public ProfileResponse(UserEntity user) {
            this.id = user.getId();
            this.email = user.getEmail();
            this.nickname = user.getNickname();
            this.profileImageUrl = user.getProfileImageUrl();
            this.role = user.getRole();
            this.isActive = user.isActive();
            this.emailVerified = user.isEmailVerified();
            this.createdAt = user.getCreatedAt();
            this.updatedAt = user.getUpdatedAt();
        }
    }
}
