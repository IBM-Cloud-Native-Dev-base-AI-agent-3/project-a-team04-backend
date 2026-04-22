package com.himedia.project_a_team04_backend.entity.forum;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_media")
@Getter
@NoArgsConstructor
public class ForumMediaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "forum_id", nullable = false)
    private Long forumId;

    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private ForumMediaType mediaType;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public ForumMediaEntity(Long forumId, ForumMediaType mediaType, String url,
                             String thumbnailUrl, int sortOrder) {
        this.forumId = forumId;
        this.mediaType = mediaType;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
        this.sortOrder = sortOrder;
    }
}
