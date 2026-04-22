package com.himedia.project_a_team04_backend.service.post;

import com.himedia.project_a_team04_backend.dto.post.PostDto;
import com.himedia.project_a_team04_backend.entity.post.PostEntity;
import com.himedia.project_a_team04_backend.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void insert(PostDto.Request request) {
        postRepository.save(PostEntity.builder()
                .userId(request.getUserId())
                .title(request.getTitle())
                .content(request.getContent())
                .build());
    }

    public List<PostDto.Response> getPost(Long userId) {
        return postRepository.findByUserIdAndIsDeletedFalse(userId)
                .stream()
                .map(post -> new PostDto.Response(
                        post.getId(),
                        post.getUserId(),
                        post.getTitle(),
                        post.getContent(),
                        post.getViewCount(),
                        post.getCreatedAt(),
                        post.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }
}
