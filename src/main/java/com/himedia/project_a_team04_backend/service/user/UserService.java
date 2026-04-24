package com.himedia.project_a_team04_backend.service.user;

import com.himedia.project_a_team04_backend.dto.user.UserDto;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.entity.user.UserRole;
import com.himedia.project_a_team04_backend.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
                .role(request.getRole() != null ? request.getRole() : UserRole.ROLE_USER)
                .build());

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

