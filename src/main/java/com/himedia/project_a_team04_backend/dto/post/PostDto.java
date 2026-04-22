package com.himedia.project_a_team04_backend.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PostDto {

    @Getter
    @NoArgsConstructor
    public static class Request {
        private Long userId;
        private String title;
        private String content;
    }

    @Getter
    @NoArgsConstructor
    public static class Response {
        private Long id;
        private Long userId;
        private String title;
        private String content;
        private int viewCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Response(Long id, Long userId, String title, String content,
                        int viewCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.userId = userId;
            this.title = title;
            this.content = content;
            this.viewCount = viewCount;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
    }
}
