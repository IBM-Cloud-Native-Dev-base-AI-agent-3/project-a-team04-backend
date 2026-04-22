package com.himedia.project_a_team04_backend.repository.post;

import com.himedia.project_a_team04_backend.entity.post.PostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long> {
    List<PostEntity> findAllByIsDeletedFalseOrderByCreatedAtDesc();
    Optional<PostEntity> findByIdAndIsDeletedFalse(Long id);
    Optional<PostEntity> findByIdAndUser_IdAndIsDeletedFalse(Long id, Long userId);
}
