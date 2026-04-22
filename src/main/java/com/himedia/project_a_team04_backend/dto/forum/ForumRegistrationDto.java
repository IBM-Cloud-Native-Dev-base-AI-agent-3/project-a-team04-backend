package com.himedia.project_a_team04_backend.dto.forum;

import com.himedia.project_a_team04_backend.entity.forum.ForumRegistrationEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumRegistrationStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ForumRegistrationDto {

    @Getter
    @NoArgsConstructor
    public static class ApplyRequest {
        private String note;
    }

    @Getter
    @NoArgsConstructor
    public static class ReviewRequest {
        private ForumRegistrationStatus status;
        private String rejectReason;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final Long forumId;
        private final Long userId;
        private final ForumRegistrationStatus status;
        private final String note;
        private final String rejectReason;
        private final Long reviewedBy;
        private final LocalDateTime reviewedAt;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public Response(ForumRegistrationEntity entity) {
            this.id = entity.getId();
            this.forumId = entity.getForum().getId();
            this.userId = entity.getUser().getId();
            this.status = entity.getStatus();
            this.note = entity.getNote();
            this.rejectReason = entity.getRejectReason();
            this.reviewedBy = entity.getReviewedByUser() != null ? entity.getReviewedByUser().getId() : null;
            this.reviewedAt = entity.getReviewedAt();
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }
    }
}
