package com.himedia.project_a_team04_backend.dto.forum;

import com.himedia.project_a_team04_backend.entity.forum.ForumMediaEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumMediaType;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ForumMediaDto {

    @Getter
    @NoArgsConstructor
    public static class Request {
        private ForumMediaType mediaType;
        private String url;
        private String thumbnailUrl;
        private int sortOrder;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final Long forumId;
        private final ForumMediaType mediaType;
        private final String url;
        private final String thumbnailUrl;
        private final int sortOrder;
        private final LocalDateTime createdAt;

        public Response(ForumMediaEntity entity) {
            this.id = entity.getId();
            this.forumId = entity.getForumId();
            this.mediaType = entity.getMediaType();
            this.url = entity.getUrl();
            this.thumbnailUrl = entity.getThumbnailUrl();
            this.sortOrder = entity.getSortOrder();
            this.createdAt = entity.getCreatedAt();
        }
    }
}
