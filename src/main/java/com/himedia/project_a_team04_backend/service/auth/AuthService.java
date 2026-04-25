package com.himedia.project_a_team04_backend.service.auth;

import com.himedia.project_a_team04_backend.config.JwtUtil;
import com.himedia.project_a_team04_backend.dto.auth.AuthDto;
import com.himedia.project_a_team04_backend.entity.auth.EmailVerificationEntity;
import com.himedia.project_a_team04_backend.entity.auth.PasswordResetEntity;
import com.himedia.project_a_team04_backend.entity.auth.RefreshTokenEntity;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.repository.auth.EmailVerificationRepository;
import com.himedia.project_a_team04_backend.repository.auth.PasswordResetRepository;
import com.himedia.project_a_team04_backend.repository.auth.RefreshTokenRepository;
import com.himedia.project_a_team04_backend.repository.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final BrevoEmailService brevoEmailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    // AuthenticationManager가 내부적으로 UserDetailsService(=AuthService)를 사용하는
    // 순환 의존성을 @Lazy로 해결
    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       EmailVerificationRepository emailVerificationRepository,
                       PasswordResetRepository passwordResetRepository,
                       @Lazy AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil,
                       @Lazy PasswordEncoder passwordEncoder,
                       BrevoEmailService brevoEmailService) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.emailVerificationRepository = emailVerificationRepository;
        this.passwordResetRepository = passwordResetRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.brevoEmailService = brevoEmailService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(user.getRole().name())
                .build();
    }

    @Transactional
    public AuthDto.TokenResponse login(AuthDto.LoginRequest request, String deviceInfo) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (user.isDeleted()) {
            throw new IllegalStateException("탈퇴한 계정입니다.");
        }
        if (!user.isActive()) {
            throw new IllegalStateException("비활성화된 계정입니다.");
        }
        if (!user.isEmailVerified()) {
            throw new IllegalStateException("이메일 인증이 필요합니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());

        refreshTokenRepository.save(RefreshTokenEntity.builder()
                .user(user)
                .tokenHash(refreshToken)
                .deviceInfo(deviceInfo)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build());

        return new AuthDto.TokenResponse(accessToken, refreshToken);
    }

    @Transactional
    public String verifyEmail(String token) {
        EmailVerificationEntity verification = emailVerificationRepository.findByTokenHash(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 인증 토큰입니다."));

        if (verification.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("만료된 인증 토큰입니다.");
        }
        if (verification.isVerified()) {
            throw new IllegalStateException("이미 인증된 토큰입니다.");
        }

        verification.markVerified();
        verification.getUser().verifyEmail();

        return frontendUrl + "/login?verified=true";
    }

    @Transactional
    public void sendPasswordReset(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new EntityNotFoundException("가입된 이메일이 없습니다."));

        String token = java.util.UUID.randomUUID().toString();
        passwordResetRepository.save(PasswordResetEntity.builder()
                .user(user)
                .tokenHash(token)
                .expiredAt(LocalDateTime.now().plusMinutes(30))
                .build());

        brevoEmailService.sendPasswordResetEmail(email, token);
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetEntity reset = passwordResetRepository.findByTokenHash(token)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));

        if (reset.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("만료된 토큰입니다.");
        }
        if (reset.isUsed()) {
            throw new IllegalStateException("이미 사용된 토큰입니다.");
        }

        reset.markUsed();
        reset.getUser().changePassword(passwordEncoder.encode(newPassword));
    }

    @Transactional
    public void logout(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        refreshTokenRepository.deleteByUser_Id(user.getId());
    }
}
