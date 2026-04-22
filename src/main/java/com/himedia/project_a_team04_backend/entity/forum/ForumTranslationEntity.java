package com.himedia.project_a_team04_backend.entity.forum;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_translations")
@Getter
@NoArgsConstructor
public class ForumTranslationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CASCADE: 포럼 삭제 시 번역 정보 함께 삭제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private ForumEntity forum;

    @Column(nullable = false, length = 10)
    private String locale;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 255)
    private String location;

    @Column(columnDefinition = "TEXT")
    private String speakers;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ForumTranslationEntity(ForumEntity forum, String locale, String title,
                                   String description, String location, String speakers) {
        this.forum = forum;
        this.locale = locale;
        this.title = title;
        this.description = description;
        this.location = location;
        this.speakers = speakers;
    }
}
