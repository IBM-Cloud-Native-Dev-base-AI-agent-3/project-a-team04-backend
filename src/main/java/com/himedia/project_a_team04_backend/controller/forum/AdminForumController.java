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
@RequestMapping("/admin/forums")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AdminForumController {

    private final ForumService forumService;
    private final ForumRegistrationService forumRegistrationService;

    @PostMapping
    public ResponseEntity<ForumDto.Response> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ForumDto.CreateRequest request,
            @RequestParam(defaultValue = "ko") String locale
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(forumService.create(userDetails.getUsername(), request, locale));
    }

    @PatchMapping("/{forumId}")
    public ResponseEntity<ForumDto.Response> update(
            @PathVariable Long forumId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ForumDto.CreateRequest request,
            @RequestParam(defaultValue = "ko") String locale
    ) {
        return ResponseEntity.ok(forumService.update(userDetails.getUsername(), forumId, request, locale));
    }

    @DeleteMapping("/{forumId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long forumId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        forumService.delete(userDetails.getUsername(), forumId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{forumId}/registrations")
    public ResponseEntity<List<ForumRegistrationDto.Response>> getRegistrations(
            @PathVariable Long forumId
    ) {
        return ResponseEntity.ok(forumRegistrationService.getList(forumId));
    }

    @PatchMapping("/{forumId}/registrations/{registrationId}/review")
    public ResponseEntity<ForumRegistrationDto.Response> review(
            @PathVariable Long forumId,
            @PathVariable Long registrationId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ForumRegistrationDto.ReviewRequest request
    ) {
        return ResponseEntity.ok(
                forumRegistrationService.review(forumId, registrationId, userDetails.getUsername(), request)
        );
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
