package com.malback.humor.dto.humorPostDto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HumorPostRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String email;

    @NotBlank
    private String type;
}
