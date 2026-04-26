package com.himedia.project_a_team04_backend.service.post;

import com.himedia.project_a_team04_backend.dto.post.PostDto;
import com.himedia.project_a_team04_backend.dto.post.PostDetailDto;
import com.himedia.project_a_team04_backend.dto.post.PostModifyDto;
import com.himedia.project_a_team04_backend.entity.post.PostEntity;
import com.himedia.project_a_team04_backend.entity.post.PostViewEntity;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.repository.post.PostRepository;
import com.himedia.project_a_team04_backend.repository.post.PostViewRepository;
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
    private final PostViewRepository postViewRepository;
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

    @Transactional
    public PostDetailDto.Response getPostDetail(Long postId, String userEmail, String ipAddress) {
        PostEntity post = postRepository.findByIdAndIsDeletedFalse(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));

        if (userEmail != null) {
            userRepository.findByEmail(userEmail)
                    .filter(u -> !u.isDeleted())
                    .ifPresent(user -> {
                        if (!postViewRepository.existsByPost_IdAndUser_Id(postId, user.getId())) {
                            postViewRepository.save(PostViewEntity.builder()
                                    .post(post)
                                    .user(user)
                                    .ipAddress(ipAddress)
                                    .build());
                            post.increaseViewCount();
                        }
                    });
        }

        return new PostDetailDto.Response(post);
    }

    @Transactional
    public PostModifyDto.Response update(Long postId, Long userId, PostModifyDto.Request request) {
        PostEntity post = postRepository.findByIdAndUser_IdAndIsDeletedFalse(postId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));

        post.modify(request.getTitle(), request.getContent());
        return new PostModifyDto.Response(post);
    }

    @Transactional
    public void delete(Long postId, Long userId) {
        PostEntity post = postRepository.findByIdAndUser_IdAndIsDeletedFalse(postId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found: " + postId));

        post.softDelete();
    }
}
