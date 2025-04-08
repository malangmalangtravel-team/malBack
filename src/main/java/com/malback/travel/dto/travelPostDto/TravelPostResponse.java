package com.malback.travel.dto.travelPostDto;

import com.malback.travel.entity.TravelPost;
import com.malback.travel.enums.BoardType;
import com.malback.user.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TravelPostResponse {
    private Long id;
    private String countryName;
    private BoardType type;
    private String title;
    private String content;
    private int viewCount;
    private LocalDateTime createdAt;
    private String email;
    private String nickname;

    public static TravelPostResponse fromEntity(TravelPost travelPost, UserRepository userRepository) {
        String nickname = userRepository.findByEmail(travelPost.getEmail())
                .map(user -> user.getNickname())
                .orElse("알 수 없음");

        return TravelPostResponse.builder()
                .id(travelPost.getId())
                .countryName(travelPost.getCountry().getCountryName())
                .type(BoardType.valueOf(travelPost.getType().name()))
                .title(travelPost.getTitle())
                .content(travelPost.getContent())
                .viewCount(travelPost.getViewCount())
                .createdAt(travelPost.getCreatedAt())
                .email(travelPost.getEmail())
                .nickname(nickname)
                .build();
    }

    public static TravelPostResponse fromEntity(TravelPost travelPost) {
        return TravelPostResponse.builder()
                .id(travelPost.getId())
                .countryName(travelPost.getCountry().getCountryName())
                .type(BoardType.valueOf(travelPost.getType().name()))
                .title(travelPost.getTitle())
                .content(travelPost.getContent())
                .viewCount(travelPost.getViewCount())
                .createdAt(travelPost.getCreatedAt())
                .email(travelPost.getEmail())
                .nickname(null) // 작성에서는 닉네임 필요 없음
                .build();
    }
}
