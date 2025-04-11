package com.malback.travel.dto.travelCommentDto;

import com.malback.travel.entity.TravelComment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelCommentResponseDto {
    private Long id;
    private Long postId;
    private String email;
    private String nickname;
    private String content;
    private Long parentCommentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<TravelCommentResponseDto> children = new ArrayList<>();

    public static TravelCommentResponseDto fromEntity(TravelComment comment) {
        return TravelCommentResponseDto.builder()
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
