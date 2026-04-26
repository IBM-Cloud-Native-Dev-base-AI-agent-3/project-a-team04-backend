package com.himedia.project_a_team04_backend.repository.forum;

import com.himedia.project_a_team04_backend.entity.forum.ForumRegistrationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ForumRegistrationRepository extends JpaRepository<ForumRegistrationEntity, Long> {
    boolean existsByForum_IdAndUser_Id(Long forumId, Long userId);
    Optional<ForumRegistrationEntity> findByForum_IdAndUser_Id(Long forumId, Long userId);
    Optional<ForumRegistrationEntity> findByIdAndForum_Id(Long id, Long forumId);
    List<ForumRegistrationEntity> findByForum_Id(Long forumId);
}
