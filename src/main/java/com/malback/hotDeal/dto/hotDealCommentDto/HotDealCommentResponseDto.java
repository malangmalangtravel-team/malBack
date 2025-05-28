package com.malback.hotDeal.dto.hotDealCommentDto;

import com.malback.hotDeal.entity.HotDealComment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotDealCommentResponseDto {
    private Long id;
    private Long postId;
    private String email;
    private String nickname;
    private String content;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<HotDealCommentResponseDto> children = new ArrayList<>();

    public static HotDealCommentResponseDto fromEntity(HotDealComment comment) {
        return HotDealCommentResponseDto.builder()
                .id(comment.getId())
                .postId(comment.getPostId())
                .email(comment.getEmail())
                .nickname(comment.getNickname())
                .content(comment.getContent())
                .parentCommentId(comment.getParentCommentId())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .children(new ArrayList<>())
                .build();
    }
}
