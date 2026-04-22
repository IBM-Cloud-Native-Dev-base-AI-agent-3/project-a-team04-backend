package com.himedia.project_a_team04_backend.entity.forum;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "forums")
@Getter
@NoArgsConstructor
public class ForumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ForumStatus status;

    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;

    @Column(name = "thumbnail_url", length = 500)
    private String thumbnailUrl;

    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ForumEntity(String slug, ForumStatus status, LocalDateTime eventDate,
                       String thumbnailUrl, int maxParticipants, Long createdBy) {
        this.slug = slug;
        this.status = status;
        this.eventDate = eventDate;
        this.thumbnailUrl = thumbnailUrl;
        this.maxParticipants = maxParticipants;
        this.createdBy = createdBy;
    }
}
