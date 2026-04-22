package com.himedia.project_a_team04_backend.repository.post;

import com.himedia.project_a_team04_backend.entity.post.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findByUser_IdAndIsDeletedFalse(Long userId);
}
