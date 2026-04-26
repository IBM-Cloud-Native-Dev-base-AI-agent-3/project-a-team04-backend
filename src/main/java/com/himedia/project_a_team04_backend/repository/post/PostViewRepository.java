package com.himedia.project_a_team04_backend.repository.post;

import com.himedia.project_a_team04_backend.entity.post.PostViewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostViewRepository extends JpaRepository<PostViewEntity, Long> {
    boolean existsByPost_IdAndUser_Id(Long postId, Long userId);
}
