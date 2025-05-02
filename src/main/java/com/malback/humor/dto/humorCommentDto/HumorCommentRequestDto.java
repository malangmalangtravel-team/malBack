package com.malback.humor.dto.humorCommentDto;

import com.malback.humor.entity.HumorComment;
import com.malback.humor.entity.HumorPost;
import com.malback.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HumorCommentRequestDto {
    private Long postId;
    private String email;
    private String content;
    private Long parentCommentId;

    public HumorComment toEntity(HumorPost post, User user, HumorComment parent) {
        return HumorComment.builder()
                .humorPost(post)
                .user(user)
                .content(content)
                .parentComment(parent)
                .build();
    }
}
