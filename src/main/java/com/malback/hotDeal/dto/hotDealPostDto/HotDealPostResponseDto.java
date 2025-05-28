package com.malback.hotDeal.dto.hotDealPostDto;

import com.malback.hotDeal.entity.HotDealPost;
import com.malback.hotDeal.enums.HotDealBoardType;
import com.malback.user.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HotDealPostResponseDto {
    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createdAt;
    private String email;
    private String nickname;
    private HotDealBoardType type;

    public static HotDealPostResponseDto fromEntity(HotDealPost post, UserRepository userRepository) {
        String nickname = userRepository.findByEmail(post.getEmail())
                .map(user -> user.getNickname())
                .orElse("알 수 없음");

        return HotDealPostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .email(post.getEmail())
                .nickname(nickname)
                .type(post.getType())
                .build();
    }
}
