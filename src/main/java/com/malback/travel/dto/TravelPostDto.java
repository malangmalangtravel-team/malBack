package com.malback.travel.dto;

import com.malback.travel.entity.Country;
import com.malback.travel.entity.TravelPost;
import com.malback.travel.enums.BoardType;
import jakarta.persistence.EntityManager;
import lombok.*;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelPostDto {
    private Long id;
    private String countryName;
    private String type;
    private String title;
    private String content;
    private Integer viewCount;
    private String email;
    private LocalDateTime deletedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TravelPostDto fromEntity(TravelPost travelPost) {
        return TravelPostDto.builder()
                .id(travelPost.getId())
                .countryName(travelPost.getCountry().getCountryName())
                .type(travelPost.getType().name())
                .title(travelPost.getTitle())
                .content(travelPost.getContent())
                .viewCount(travelPost.getViewCount())
                .email(travelPost.getEmail())
                .deletedAt(travelPost.getDeletedAt())
                .createdAt(travelPost.getCreatedAt())
                .updatedAt(travelPost.getUpdatedAt())
                .build();
    }

    public TravelPost toEntity(EntityManager entityManager) {
        // countryName을 기준으로 Country 객체를 찾음
        Country country = entityManager.createQuery("SELECT c FROM Country c WHERE c.countryName = :countryName", Country.class)
                .setParameter("countryName", this.countryName)
                .getResultStream()
                .findFirst()
                .orElse(null);  // 결과가 없으면 null 반환

        return TravelPost.builder()
                .id(this.id)
                .country(country)
                .type(BoardType.valueOf(this.type))
                .title(this.title)
                .content(this.content)
                .viewCount(this.viewCount != null ? this.viewCount : 0) // 조회수 기본값 0
                .email(this.email)
                .deletedAt(this.deletedAt) // 삭제일시 처리
                .createdAt(this.createdAt != null ? this.createdAt : LocalDateTime.now()) // 생성일시 처리
                .updatedAt(this.updatedAt != null ? this.updatedAt : LocalDateTime.now()) // 수정일시 처리
                .build();
    }
}