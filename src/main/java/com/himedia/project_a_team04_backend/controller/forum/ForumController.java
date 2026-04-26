package com.himedia.project_a_team04_backend.controller.forum;

import com.himedia.project_a_team04_backend.dto.forum.ForumDto;
import com.himedia.project_a_team04_backend.dto.forum.ForumRegistrationDto;
import com.himedia.project_a_team04_backend.service.forum.ForumRegistrationService;
import com.himedia.project_a_team04_backend.service.forum.ForumService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/forums")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;
    private final ForumRegistrationService forumRegistrationService;

    @GetMapping
    public ResponseEntity<List<ForumDto.ListResponse>> getList(
            @RequestParam(defaultValue = "ko") String locale
    ) {
        return ResponseEntity.ok(forumService.getList(locale));
    }

    @GetMapping("/{forumId}")
    public ResponseEntity<ForumDto.Response> getDetail(
            @PathVariable Long forumId,
            @RequestParam(defaultValue = "ko") String locale
    ) {
        return ResponseEntity.ok(forumService.getDetail(forumId, locale));
    }

    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{forumId}/my-registration")
    public ResponseEntity<ForumRegistrationDto.Response> getMyRegistration(
            @PathVariable Long forumId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        return ResponseEntity.ok(forumRegistrationService.getMyRegistration(forumId, userDetails.getUsername()));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{forumId}/registrations")
    public ResponseEntity<ForumRegistrationDto.Response> apply(
            @PathVariable Long forumId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ForumRegistrationDto.ApplyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(forumRegistrationService.apply(forumId, userDetails.getUsername(), request));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", e.getMessage()));
    }
}
