package com.malback.travel.dto;

import com.malback.travel.entity.TravelComment;
import com.malback.travel.entity.TravelPost;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelCommentDto {
    private Long id;
    private Long postId;  // TravelPost의 ID로 대체
    private String email;
    private String content;
    private Long parentCommentId;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TravelCommentDto fromEntity(TravelComment travelComment) {
        return TravelCommentDto.builder()
                .id(travelComment.getId())
                .postId(travelComment.getTravelPost().getId())  // travelPost에서 postId를 가져옴
                .email(travelComment.getEmail())
                .content(travelComment.getContent())
                .parentCommentId(travelComment.getParentComment() != null ? travelComment.getParentComment().getId() : null)  // 부모 댓글이 있으면 ID를 가져옴
                .deletedAt(travelComment.getDeletedAt())
                .createdAt(travelComment.getCreatedAt())
                .updatedAt(travelComment.getUpdatedAt())
                .build();
    }

    public TravelComment toEntity(TravelPost travelPost) {
        TravelComment parentComment = null;

        // 부모 댓글 설정 (부모 댓글이 있다면 실제 엔티티로 설정)
        if (this.parentCommentId != null) {
            parentComment = new TravelComment();
            parentComment.setId(this.parentCommentId);
        }

        return TravelComment.builder()
                .id(this.id)
                .travelPost(travelPost)  // TravelPost 엔티티를 설정
                .email(this.email)
                .content(this.content)
                .parentComment(parentComment)  // 부모 댓글 설정
                .build();
    }
}