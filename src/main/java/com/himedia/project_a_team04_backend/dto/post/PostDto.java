package com.himedia.project_a_team04_backend.dto.post;

import com.himedia.project_a_team04_backend.entity.post.PostEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PostDto {

    @Getter
    @NoArgsConstructor
    public static class Request {
        private String title;
        private String content;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final Long userId;
        private final String nickname;
        private final String title;
        private final String content;
        private final int viewCount;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;

        public Response(PostEntity post) {
            this.id = post.getId();
            this.userId = post.getUser().getId();
            this.nickname = post.getUser().getNickname();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.viewCount = post.getViewCount();
            this.createdAt = post.getCreatedAt();
            this.updatedAt = post.getUpdatedAt();
        }
    }
}
