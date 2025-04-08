package com.malback.travel.dto;

import com.malback.travel.entity.Country;
import com.malback.travel.entity.TravelBoard;
import com.malback.travel.enums.BoardType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelBoardDto {
    private Long id;
    private String countryName;
    private String type;
    private String boardName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TravelBoardDto fromEntity(TravelBoard travelBoard) {
        return TravelBoardDto.builder()
                .id(travelBoard.getId())
                .countryName(travelBoard.getCountry().getCountryName()) // 이제 getCountryName() 사용 가능
                .type(travelBoard.getType().name())
                .boardName(travelBoard.getBoardName())
                .build();
    }

    public TravelBoard toEntity() {
        return TravelBoard.builder()
                .id(this.id)
                .country(new Country(this.countryName)) // Country 객체 생성
                .type(BoardType.valueOf(this.type))
                .boardName(this.boardName)
                .build();
    }
}