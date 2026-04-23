package com.himedia.project_a_team04_backend.service.user;

import com.himedia.project_a_team04_backend.dto.user.UserDto;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.entity.user.UserRole;
import com.himedia.project_a_team04_backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto.ProfileResponse signup(UserDto.SignupRequest request) {
        validateSignupRequest(request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already exists: " + request.getEmail());
        }

        UserEntity savedUser = userRepository.save(UserEntity.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .nickname(request.getNickname())
                .role(request.getRole() != null ? request.getRole() : UserRole.ROLE_USER)
                .build());

        return new UserDto.ProfileResponse(savedUser);
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

