package com.himedia.project_a_team04_backend.service.post;

import com.himedia.project_a_team04_backend.dto.post.PostDto;
import com.himedia.project_a_team04_backend.entity.post.PostEntity;
import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import com.himedia.project_a_team04_backend.repository.post.PostRepository;
import com.himedia.project_a_team04_backend.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // TODO: Security 적용 후 UserEntity를 @AuthenticationPrincipal로 직접 주입받도록 변경
    public void insert(Long userId, PostDto.Request request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        postRepository.save(PostEntity.builder()
                .user(user)
                .title(request.getTitle())
                .content(request.getContent())
                .build());
    }

    public List<PostDto.Response> getPost(Long userId) {
        return postRepository.findByUser_IdAndIsDeletedFalse(userId)
                .stream()
                .map(PostDto.Response::new)
                .collect(Collectors.toList());
    }
}
