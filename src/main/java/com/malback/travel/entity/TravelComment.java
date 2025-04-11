package com.malback.travel.entity;

import com.malback.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "travel_comment")
public class TravelComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 게시글 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private TravelPost travelPost;

    // 작성자 이메일 (User와 외래키 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", referencedColumnName = "email", nullable = false)
    private User user;

    // 댓글 내용
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    // 부모 댓글 (대댓글용)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private TravelComment parentComment;

    // 논리적 삭제용
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // 생성일
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 수정일
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 부모 댓글 ID 반환
    public Long getParentCommentId() {
        return (parentComment != null) ? parentComment.getId() : null;
    }

    // 게시글 ID 반환
    public Long getPostId() {
        return travelPost.getId();
    }

    // 작성자 이메일 반환 (User에서)
    public String getEmail() {
        return user.getEmail();
    }

    // 닉네임
    public String getNickname() {
        return user.getNickname();
    }
}
