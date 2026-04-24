package com.himedia.project_a_team04_backend.repository.forum;

import com.himedia.project_a_team04_backend.entity.forum.ForumMediaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ForumMediaRepository extends JpaRepository<ForumMediaEntity, Long> {
    List<ForumMediaEntity> findByForum_Id(Long forumId);
}
