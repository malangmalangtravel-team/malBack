package com.malback.support.dto;

import com.malback.support.entity.Support;
import com.malback.support.enums.SupportBoardType;
import com.malback.user.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SupportResponseDto {
    private Long id;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createdAt;
    private String email;
    private String nickname;
    private SupportBoardType type;

    public static SupportResponseDto fromEntity(Support post, UserRepository userRepository) {
        String nickname = userRepository.findByEmail(post.getEmail())
                .map(user -> user.getNickname())
                .orElse("알 수 없음");

        return SupportResponseDto.builder()
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
