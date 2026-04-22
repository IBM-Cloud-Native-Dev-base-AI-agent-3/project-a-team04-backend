package com.himedia.project_a_team04_backend.service.post;

import com.himedia.project_a_team04_backend.dto.post.PostDto;
import com.himedia.project_a_team04_backend.dto.post.PostDetailDto;
import com.himedia.project_a_team04_backend.entity.post.PostEntity;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.repository.post.PostRepository;
import com.himedia.project_a_team04_backend.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // TODO: Security 적용 후 userId 제거, @AuthenticationPrincipal UserEntity로 교체
    @Transactional
    public PostDto.Response insert(Long userId, PostDto.Request request) {
        UserEntity user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        PostEntity savedPost = postRepository.save(PostEntity.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build());

        return new PostDto.Response(savedPost);
    }

    @Transactional(readOnly = true)
    public List<PostDto.Response> getAllPosts() {
        return postRepository.findAllByIsDeletedFalseOrderByCreatedAtDesc()
                .stream()
                .map(PostDto.Response::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostDetailDto.Response getPostDetail(Long postId) {
        PostEntity post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));
        return new PostDetailDto.Response(post);
    }
}
