package com.example.spring_practice.domain.comment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CommentResponseDto {
    @Schema(description = "댓글ID", example = "1")
    private Long commentId;
    @Schema(description = "댓글 작성자 닉네임", example = "김초코")
    private String author;
    @Schema(description = "댓글 작성자 프로필 이미지", example = "https://asdf")
    private String authorProfileImg;
    @Schema(description = "댓글 작성 시각", example = "2025-10-21 12:00:00")
    private LocalDateTime postedTime;
    @Schema(description = "댓글 내용", example = "이사람 강도야~")
    private String content;
    @Schema(description = "내가 작성자인지 여부", example = "true")
    private boolean mine;
}
