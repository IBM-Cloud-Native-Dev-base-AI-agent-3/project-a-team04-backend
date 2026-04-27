package com.himedia.project_a_team04_backend.service.user;

import com.himedia.project_a_team04_backend.dto.user.UserDto;
import com.himedia.project_a_team04_backend.entity.auth.EmailVerificationEntity;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.entity.user.UserRole;
import com.himedia.project_a_team04_backend.entity.user.UserWithdrawalEntity;
import com.himedia.project_a_team04_backend.repository.auth.EmailVerificationRepository;
import com.himedia.project_a_team04_backend.repository.auth.RefreshTokenRepository;
import com.himedia.project_a_team04_backend.repository.user.UserRepository;
import com.himedia.project_a_team04_backend.repository.user.UserWithdrawalRepository;
import com.himedia.project_a_team04_backend.service.auth.BrevoEmailService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final int WITHDRAWAL_COOLDOWN_DAYS = 30;

    private final UserRepository userRepository;
    private final UserWithdrawalRepository userWithdrawalRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final BrevoEmailService brevoEmailService;

    @Transactional
    public UserDto.ProfileResponse signup(UserDto.SignupRequest request) {
        validateSignupRequest(request);

        // 현재 활성 계정 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("이미 사용 중인 이메일입니다.");
        }

        // 탈퇴 이력 기반 재가입 30일 제한 확인
        userWithdrawalRepository.findTopByEmailOrderByWithdrawnAtDesc(request.getEmail())
                .ifPresent(withdrawal -> {
                    LocalDateTime cooldownEnd = withdrawal.getWithdrawnAt().plusDays(WITHDRAWAL_COOLDOWN_DAYS);
                    if (LocalDateTime.now().isBefore(cooldownEnd)) {
                        throw new IllegalStateException(
                                "탈퇴 후 " + WITHDRAWAL_COOLDOWN_DAYS + "일 이내에는 재가입이 불가합니다.");
                    }
                });

        UserEntity savedUser = userRepository.save(UserEntity.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .role(UserRole.ROLE_USER)
                .build());

        String token = UUID.randomUUID().toString();
        emailVerificationRepository.save(EmailVerificationEntity.builder()
                .user(savedUser)
                .tokenHash(token)
                .expiredAt(LocalDateTime.now().plusMinutes(30))
                .build());

        brevoEmailService.sendVerificationEmail(savedUser.getEmail(), token);

        return new UserDto.ProfileResponse(savedUser);
    }

    public UserDto.ProfileResponse getProfile(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new UserDto.ProfileResponse(user);
    }

    @Transactional
    public UserDto.ProfileResponse updateProfile(String email, UserDto.UpdateProfileRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.updateProfile(request.getNickname(), request.getProfileImageUrl());
        return new UserDto.ProfileResponse(user);
    }

    @Transactional
    public void withdraw(String email, UserDto.WithdrawalRequest request) {
        UserEntity user = userRepository.findByEmail(email)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        String originalEmail = user.getEmail();
        LocalDateTime now = LocalDateTime.now();

        userWithdrawalRepository.save(UserWithdrawalEntity.builder()
                .user(user)
                .email(originalEmail)
                .reason(request.getReason())
                .withdrawnAt(now)
                .build());

        refreshTokenRepository.deleteByUser_Id(user.getId());
        user.withdraw();
    }

    private void validateSignupRequest(UserDto.SignupRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body is required");
        }
        if (isBlank(request.getEmail()) || isBlank(request.getPassword()) || isBlank(request.getNickname())) {
            throw new IllegalArgumentException("email, password, nickname are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
