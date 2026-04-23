package com.himedia.project_a_team04_backend.repository.forum;

import com.himedia.project_a_team04_backend.entity.forum.ForumEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumRepository extends JpaRepository<ForumEntity, Long> {
}
