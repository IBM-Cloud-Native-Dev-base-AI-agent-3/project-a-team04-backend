package com.himedia.project_a_team04_backend.controller.forum;

import com.himedia.project_a_team04_backend.dto.forum.ForumDto;
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

    @SecurityRequirement(name = "bearerAuth")
    @PostMapping
    public ResponseEntity<ForumDto.Response> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ForumDto.CreateRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(forumService.create(userDetails.getUsername(), request));
    }

    @GetMapping
    public ResponseEntity<List<ForumDto.ListResponse>> getList(
            @RequestParam(defaultValue = "ko") String locale
    ) {
        return ResponseEntity.ok(forumService.getList(locale));
    }

    @GetMapping("/{forumId}")
    public ResponseEntity<ForumDto.Response> getDetail(@PathVariable Long forumId) {
        return ResponseEntity.ok(forumService.getDetail(forumId));
    }

    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/{forumId}")
    public ResponseEntity<ForumDto.Response> update(
            @PathVariable Long forumId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ForumDto.CreateRequest request
    ) {
        return ResponseEntity.ok(forumService.update(userDetails.getUsername(), forumId, request));
    }

    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{forumId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long forumId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        forumService.delete(userDetails.getUsername(), forumId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }
}
