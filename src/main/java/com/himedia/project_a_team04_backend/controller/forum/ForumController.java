package com.himedia.project_a_team04_backend.controller.forum;

import com.himedia.project_a_team04_backend.dto.forum.ForumDto;
import com.himedia.project_a_team04_backend.exception.forum.ForumAccessDeniedException;
import com.himedia.project_a_team04_backend.service.forum.ForumService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/forums")
@RequiredArgsConstructor
public class ForumController {

    private final ForumService forumService;

    // TODO: Security 적용 후 @RequestParam userId 제거, @AuthenticationPrincipal로 교체
    @PostMapping
    public ResponseEntity<ForumDto.Response> create(
            @RequestParam Long userId,
            @RequestBody ForumDto.CreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(forumService.create(userId, request));
    }

    @ExceptionHandler(ForumAccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(ForumAccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }
}
