package com.example.spring_practice.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DuplicateCheckResponseDto {
    @Schema(description = "중복 여부", example = "true")
    private boolean duplicated;
}
