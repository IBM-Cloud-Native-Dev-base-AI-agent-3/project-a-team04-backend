package com.himedia.project_a_team04_backend.dto.forum;

import com.himedia.project_a_team04_backend.entity.forum.ForumEntity;
import com.himedia.project_a_team04_backend.entity.forum.ForumStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ForumDto {

    @Getter
    @NoArgsConstructor
    public static class CreateRequest {
        private String slug;
        private ForumStatus status;
        private LocalDateTime eventDate;
        private String thumbnailUrl;
        private int maxParticipants;
        private List<ForumTranslationDto.Request> translations;
        private List<ForumMediaDto.Request> media;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateRequest {
        private ForumStatus status;
        private LocalDateTime eventDate;
        private String thumbnailUrl;
        private int maxParticipants;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String slug;
        private final ForumStatus status;
        private final String statusLabel;
        private final LocalDateTime eventDate;
        private final String thumbnailUrl;
        private final int maxParticipants;
        private final Long createdBy;
        private final List<ForumTranslationDto.Response> translations;
        private final List<ForumMediaDto.Response> media;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public Response(ForumEntity entity,
                        List<ForumTranslationDto.Response> translations,
                        List<ForumMediaDto.Response> media,
                        String locale) {
            this.id = entity.getId();
            this.slug = entity.getSlug();
            this.status = entity.getStatus();
            this.statusLabel = entity.getStatus().getLabel(locale);
            this.eventDate = entity.getEventDate();
            this.thumbnailUrl = entity.getThumbnailUrl();
            this.maxParticipants = entity.getMaxParticipants();
            this.createdBy = entity.getCreatedByUser().getId();
            this.translations = translations;
            this.media = media;
            this.createdAt = entity.getCreatedAt();
            this.updatedAt = entity.getUpdatedAt();
        }
    }

    @Getter
    public static class ListResponse {
        private final Long id;
        private final String slug;
        private final ForumStatus status;
        private final String statusLabel;
        private final LocalDateTime eventDate;
        private final String thumbnailUrl;
        private final int maxParticipants;
        private final String title;
        private final String location;
        private final String speakers;
        private final LocalDateTime createdAt;

        public ListResponse(ForumEntity entity, String title, String location, String speakers, String locale) {
            this.id = entity.getId();
            this.slug = entity.getSlug();
            this.status = entity.getStatus();
            this.statusLabel = entity.getStatus().getLabel(locale);
            this.eventDate = entity.getEventDate();
            this.thumbnailUrl = entity.getThumbnailUrl();
            this.maxParticipants = entity.getMaxParticipants();
            this.title = title;
            this.location = location;
            this.speakers = speakers;
            this.createdAt = entity.getCreatedAt();
        }
    }
}
