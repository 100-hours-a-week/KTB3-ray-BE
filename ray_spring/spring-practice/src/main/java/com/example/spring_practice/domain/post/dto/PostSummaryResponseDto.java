package com.example.spring_practice.domain.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class PostSummaryResponseDto {
    @Schema(description = "게시글 ID", example = "1")
    private Long postId;
    @Schema(description = "게시글 제목", example = "돈이 없습니다.")
    private String title;
    @Schema(description = "게시글 작성 시각", example = "2025-10-23 15:35:43")
    private LocalDateTime postedTime;
    @Schema(description = "게시글 작성자 닉네임", example = "김뽀삐")
    private String author;
    @Schema(description = "게시글 작성자 프로필", example = "https://sampleimge")
    private String authorProfileImg;
    @Schema(description = "게시글 좋아요수", example = "5")
    private long likeCount;
    @Schema(description = "게시글 조회수", example = "5")
    private long viewCount;
    @Schema(description = "게시글 댓글수", example = "5")
    private long commentCount;
    @Schema(description = "내가 좋아요 눌렀는지 여부", example = "true")
    private boolean isPostLiked;
}
