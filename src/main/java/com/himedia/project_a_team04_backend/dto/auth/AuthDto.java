package com.himedia.project_a_team04_backend.dto.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthDto {

    @Getter
    @NoArgsConstructor
    public static class LoginRequest {
        private String email;
        private String password;
    }

    @Getter
    public static class TokenResponse {
        private final String accessToken;
        private final String refreshToken;

        public TokenResponse(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

    @Getter
    @NoArgsConstructor
    public static class RefreshRequest {
        private String refreshToken;
    }

    @Getter
    @NoArgsConstructor
    public static class VerifyEmailRequest {
        private String token;
    }

    @Getter
    @NoArgsConstructor
    public static class SendPasswordResetRequest {
        private String email;
    }

    @Getter
    @NoArgsConstructor
    public static class ResetPasswordRequest {
        private String token;
        private String newPassword;
    }
}
