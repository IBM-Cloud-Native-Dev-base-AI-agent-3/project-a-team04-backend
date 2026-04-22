package com.himedia.project_a_team04_backend.dto.forum;

import com.himedia.project_a_team04_backend.entity.forum.ForumTranslationEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ForumTranslationDto {

    @Getter
    @NoArgsConstructor
    public static class Request {
        private String locale;
        private String title;
        private String description;
        private String location;
        private String speakers;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final Long forumId;
        private final String locale;
        private final String title;
        private final String description;
        private final String location;
        private final String speakers;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public Response(ForumTranslationEntity entity) {
            this.id = entity.getId();
            this.forumId = entity.getForum().getId();
            this.locale = entity.getLocale();
            this.title = entity.getTitle();
            this.description = entity.getDescription();
            this.location = entity.getLocation();
            this.speakers = entity.getSpeakers();
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }
    }
}
