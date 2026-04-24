package com.himedia.project_a_team04_backend.repository.forum;

import com.himedia.project_a_team04_backend.entity.forum.ForumTranslationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ForumTranslationRepository extends JpaRepository<ForumTranslationEntity, Long> {
    List<ForumTranslationEntity> findByForum_Id(Long forumId);
    Optional<ForumTranslationEntity> findByForum_IdAndLocale(Long forumId, String locale);
}
