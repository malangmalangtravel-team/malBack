package com.malback.travel.dto;

import com.malback.travel.entity.Country;
import lombok.*;

import lombok.Getter;
import lombok.Setter;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CountryDto {
    private Long id;
    private String countryName;
    private String countryImg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CountryDto fromEntity(Country country) {
        return CountryDto.builder()
                .id(country.getId())
                .countryName(country.getCountryName())
                .countryImg(country.getCountryImg())
                .build();
    }

    public Country toEntity() {
        return Country.builder()
                .id(this.id)
                .countryName(this.countryName)
                .build();
    }
}

