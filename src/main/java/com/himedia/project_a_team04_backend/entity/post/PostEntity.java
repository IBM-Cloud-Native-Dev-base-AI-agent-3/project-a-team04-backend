package com.himedia.project_a_team04_backend.entity.post;

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
@Table(name = "posts")
@Getter
@NoArgsConstructor
public class PostEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RESTRICT: 게시글이 있는 유저는 삭제 불가
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "view_count", nullable = false)
    private int viewCount = 0;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // CASCADE: 게시글 삭제 시 조회 기록 함께 삭제
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<PostViewEntity> postViews = new ArrayList<>();

    @Builder
    public PostEntity(UserEntity user, String title, String content) {
        this.user = user;
        this.title = title;
        this.content = content;
    }

    public void modify(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void softDelete() {
        this.isDeleted = true;
    }
}
