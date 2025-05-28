package com.malback.hotDeal.dto.hotDealPostDto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotDealPostRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String email;

    @NotBlank
    private String type;
}
