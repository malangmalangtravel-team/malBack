package com.malback.humor.dto.humorCommentDto;

import com.malback.humor.entity.HumorComment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HumorCommentResponseDto {
    private Long id;
    private Long postId;
    private String email;
    private String nickname;
    private String content;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<HumorCommentResponseDto> children = new ArrayList<>();

    public static HumorCommentResponseDto fromEntity(HumorComment comment) {
        return HumorCommentResponseDto.builder()
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
