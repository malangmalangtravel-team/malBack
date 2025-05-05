package com.malback.travel.dto.travelPostDto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelPostRequestDto {
    @NotBlank
    private String type;

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String email;
}
