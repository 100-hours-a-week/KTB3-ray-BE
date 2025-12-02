package com.example.spring_practice.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {
    @Schema(description = "변경할 닉네임", example = "김초코", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String content;
}
