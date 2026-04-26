package com.himedia.project_a_team04_backend.service.user;

import com.himedia.project_a_team04_backend.dto.user.UserDto;
import com.himedia.project_a_team04_backend.entity.auth.EmailVerificationEntity;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.entity.user.UserRole;
import com.himedia.project_a_team04_backend.repository.auth.EmailVerificationRepository;
import com.himedia.project_a_team04_backend.repository.user.UserRepository;
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

    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final BrevoEmailService brevoEmailService;

    @Transactional
    public UserDto.ProfileResponse signup(UserDto.SignupRequest request) {
        validateSignupRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already exists: " + request.getEmail());
        }

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
