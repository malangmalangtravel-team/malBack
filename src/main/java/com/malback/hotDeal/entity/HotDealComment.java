package com.malback.hotDeal.entity;

import com.malback.hotDeal.entity.HotDealPost;
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
@Table(name = "hotdeal_comment")
public class HotDealComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private HotDealPost hotDealPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "email", referencedColumnName = "email", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    private HotDealComment parentComment;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 부모 댓글 ID 반환
    public Long getParentCommentId() {
        return (parentComment != null) ? parentComment.getId() : null;
    }

    // 게시글 ID 반환
    public Long getPostId() {
        return hotDealPost.getId();
    }

    // 작성자 이메일 반환
    public String getEmail() {
        return user.getEmail();
    }

    // 닉네임
    public String getNickname() {
        return user.getNickname();
    }
}
