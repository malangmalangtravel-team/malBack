package com.malback.hotDeal.dto.hotDealCommentDto;

import com.malback.hotDeal.entity.HotDealComment;
import com.malback.hotDeal.entity.HotDealPost;
import com.malback.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotDealCommentRequestDto {
    private Long postId;
    private String email;
    private String content;
    private Long parentCommentId;

    public HotDealComment toEntity(HotDealPost post, User user, HotDealComment parent) {
        return HotDealComment.builder()
                .hotDealPost(post)
                .user(user)
                .content(content)
                .parentComment(parent)
                .build();
    }
}
