package com.malback.travel.dto.travelCommentDto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.malback.travel.entity.TravelComment;
import com.malback.travel.entity.TravelPost;
import com.malback.user.entity.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelCommentRequestDto {
    private Long postId;
    @JsonIgnore
    private String email;
    private String content;
    private Long parentCommentId;

    public TravelComment toEntity(TravelPost post, User user, TravelComment parent) {
        return TravelComment.builder()
                .travelPost(post)
                .user(user)
                .content(content)
                .parentComment(parent)
                .build();
    }
}
