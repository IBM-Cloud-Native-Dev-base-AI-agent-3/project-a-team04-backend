package com.himedia.project_a_team04_backend.entity.forum;

import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "forum_registrations")
@Getter
@NoArgsConstructor
public class ForumRegistrationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RESTRICT: 신청자가 있는 포럼은 삭제 불가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private ForumEntity forum;

    // CASCADE: 유저 삭제 시 포럼 신청 이력 함께 삭제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ForumRegistrationStatus status;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    // nullable: 심사 전에는 담당자 없음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private UserEntity reviewedByUser;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ForumRegistrationEntity(ForumEntity forum, UserEntity user,
                                    ForumRegistrationStatus status, String note) {
        this.forum = forum;
        this.user = user;
        this.status = status;
        this.note = note;
    }
}
