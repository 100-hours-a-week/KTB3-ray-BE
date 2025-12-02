package com.example.spring_practice.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CommentIdResponseDto {
    @Schema(description = "댓글ID", example = "1")
    private Long commentId;
}
