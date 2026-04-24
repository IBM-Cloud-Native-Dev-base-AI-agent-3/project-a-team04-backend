package com.himedia.project_a_team04_backend.service.auth;

import com.himedia.project_a_team04_backend.config.JwtUtil;
import com.himedia.project_a_team04_backend.dto.auth.AuthDto;
import com.himedia.project_a_team04_backend.entity.auth.RefreshTokenEntity;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.repository.auth.RefreshTokenRepository;
import com.himedia.project_a_team04_backend.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
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
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // AuthenticationManager가 내부적으로 UserDetailsService(=AuthService)를 사용하는
    // 순환 의존성을 @Lazy로 해결
    public AuthService(UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       @Lazy AuthenticationManager authenticationManager,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
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
}
