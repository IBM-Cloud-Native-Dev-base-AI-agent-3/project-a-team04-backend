package com.himedia.project_a_team04_backend.controller.user;

import com.himedia.project_a_team04_backend.dto.user.UserDto;
import com.himedia.project_a_team04_backend.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto.ProfileResponse> signup(@RequestBody UserDto.SignupRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.signup(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto.ProfileResponse> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfile(userDetails.getUsername()));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserDto.ProfileResponse> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UserDto.UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getUsername(), request));
    }

    @ExceptionHandler(jakarta.persistence.EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(jakarta.persistence.EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }
}


