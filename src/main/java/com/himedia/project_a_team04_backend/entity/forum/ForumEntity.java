package com.himedia.project_a_team04_backend.entity.forum;

import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    // RESTRICT: 포럼을 개설한 유저는 삭제 불가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private UserEntity createdByUser;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // CASCADE: 포럼 삭제 시 번역 정보 함께 삭제
    @OneToMany(mappedBy = "forum", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ForumTranslationEntity> translations = new ArrayList<>();

    // CASCADE: 포럼 삭제 시 미디어 함께 삭제
    @OneToMany(mappedBy = "forum", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ForumMediaEntity> media = new ArrayList<>();

    // RESTRICT: 신청자가 있는 포럼은 삭제 불가
    @OneToMany(mappedBy = "forum")
    private List<ForumRegistrationEntity> registrations = new ArrayList<>();

    // RESTRICT: 참석자가 있는 포럼은 삭제 불가
    @OneToMany(mappedBy = "forum")
    private List<ForumAttendeeEntity> attendees = new ArrayList<>();

    @Builder
    public ForumEntity(String slug, ForumStatus status, LocalDateTime eventDate,
                       String thumbnailUrl, int maxParticipants, UserEntity createdByUser) {
        this.slug = slug;
        this.status = status;
        this.eventDate = eventDate;
        this.thumbnailUrl = thumbnailUrl;
        this.maxParticipants = maxParticipants;
        this.createdByUser = createdByUser;
    }

    public void update(String slug, ForumStatus status, LocalDateTime eventDate,
                       String thumbnailUrl, int maxParticipants) {
        this.slug = slug;
        this.status = status;
        this.eventDate = eventDate;
        this.thumbnailUrl = thumbnailUrl;
        this.maxParticipants = maxParticipants;
    }
}
