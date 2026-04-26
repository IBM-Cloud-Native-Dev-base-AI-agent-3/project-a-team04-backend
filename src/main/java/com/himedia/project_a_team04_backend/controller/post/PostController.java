package com.himedia.project_a_team04_backend.controller.post;

import com.himedia.project_a_team04_backend.dto.post.PostDto;
import com.himedia.project_a_team04_backend.dto.post.PostDetailDto;
import com.himedia.project_a_team04_backend.dto.post.PostModifyDto;
import com.himedia.project_a_team04_backend.service.post.PostService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // TODO: Security 적용 후 @RequestParam userId 제거, @AuthenticationPrincipal로 교체
    @PostMapping
    public ResponseEntity<PostDto.Response> insert(@RequestParam Long userId, @RequestBody PostDto.Request request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.insert(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<PostDto.Response>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{postId}/view")
    public ResponseEntity<PostDetailDto.Response> getPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest request) {
        String userEmail = userDetails != null ? userDetails.getUsername() : null;
        String ipAddress = request.getHeader("X-Forwarded-For") != null
                ? request.getHeader("X-Forwarded-For").split(",")[0].trim()
                : request.getRemoteAddr();
        return ResponseEntity.ok(postService.getPostDetail(postId, userEmail, ipAddress));
    }

    // TODO: Security 적용 후 @RequestParam userId 제거, @AuthenticationPrincipal로 교체
    @PatchMapping("/{postId}")
    public ResponseEntity<PostModifyDto.Response> update(
            @PathVariable Long postId,
            @RequestParam Long userId,
            @RequestBody PostModifyDto.Request request
    ) {
        return ResponseEntity.ok(postService.update(postId, userId, request));
    }

    // TODO: Security 적용 후 @RequestParam userId 제거, @AuthenticationPrincipal로 교체
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(@PathVariable Long postId, @RequestParam Long userId) {
        postService.delete(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", e.getMessage()));
    }
}
