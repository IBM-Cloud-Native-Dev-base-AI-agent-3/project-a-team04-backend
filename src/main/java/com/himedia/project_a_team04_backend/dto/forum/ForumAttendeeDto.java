package com.himedia.project_a_team04_backend.dto.forum;

import com.himedia.project_a_team04_backend.entity.forum.ForumAttendeeEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumAttendeeStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ForumAttendeeDto {

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        private ForumAttendeeStatus status;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final Long forumId;
        private final Long userId;
        private final ForumAttendeeStatus status;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public Response(ForumAttendeeEntity entity) {
            this.id = entity.getId();
            this.forumId = entity.getForumId();
            this.userId = entity.getUserId();
            this.status = entity.getStatus();
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }
    }
}
