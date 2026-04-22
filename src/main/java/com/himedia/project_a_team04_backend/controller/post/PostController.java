package com.himedia.project_a_team04_backend.controller.post;

import com.himedia.project_a_team04_backend.dto.post.PostDto;
import com.himedia.project_a_team04_backend.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    // TODO: Security 적용 후 @RequestParam userId 제거, @AuthenticationPrincipal로 교체
    @PostMapping
    public ResponseEntity<Void> insert(@RequestParam Long userId, @RequestBody PostDto.Request request) {
        postService.insert(userId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PostDto.Response>> getPost(@RequestParam Long userId) {
        return ResponseEntity.ok(postService.getPost(userId));
    }
}
