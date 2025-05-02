package com.malback.humor.dto.humorPostDto;

import com.malback.humor.entity.HumorPost;
import com.malback.humor.enums.HumorBoardType;
import com.malback.user.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HumorPostResponseDto {
    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createdAt;
    private String email;
    private String nickname;
    private HumorBoardType type;

    public static HumorPostResponseDto fromEntity(HumorPost post, UserRepository userRepository) {
        String nickname = userRepository.findByEmail(post.getEmail())
                .map(user -> user.getNickname())
                .orElse("알 수 없음");

        return HumorPostResponseDto.builder()
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
