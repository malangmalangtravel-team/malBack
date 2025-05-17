package com.malback.support.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportRequestDto {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private String email;

    @NotBlank
    private String type;
}
