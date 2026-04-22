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
@Table(name = "forum_attendees")
@Getter
@NoArgsConstructor
public class ForumAttendeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RESTRICT: 참석자가 있는 포럼은 삭제 불가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private ForumEntity forum;

    // CASCADE: 유저 삭제 시 포럼 참석 정보 함께 삭제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ForumAttendeeStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public ForumAttendeeEntity(ForumEntity forum, UserEntity user, ForumAttendeeStatus status) {
        this.forum = forum;
        this.user = user;
        this.status = status;
    }
}
