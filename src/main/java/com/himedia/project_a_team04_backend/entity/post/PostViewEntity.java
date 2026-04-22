package com.himedia.project_a_team04_backend.entity.post;

import com.himedia.project_a_team04_backend.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "post_views",
        uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "user_id"})
)
@Getter
@NoArgsConstructor
public class PostViewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CASCADE: 게시글 삭제 시 조회 기록 함께 삭제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

    // CASCADE: 유저 삭제 시 조회 기록 함께 삭제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "ip_address", nullable = false, length = 255)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public PostViewEntity(PostEntity post, UserEntity user, String ipAddress) {
        this.post = post;
        this.user = user;
        this.ipAddress = ipAddress;
    }
}
