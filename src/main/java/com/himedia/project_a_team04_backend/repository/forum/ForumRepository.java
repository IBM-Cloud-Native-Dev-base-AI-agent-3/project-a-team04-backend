package com.himedia.project_a_team04_backend.repository.forum;

import com.himedia.project_a_team04_backend.entity.forum.ForumEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumRepository extends JpaRepository<ForumEntity, Long> {
    List<ForumEntity> findAllByOrderByCreatedAtDesc();
}
