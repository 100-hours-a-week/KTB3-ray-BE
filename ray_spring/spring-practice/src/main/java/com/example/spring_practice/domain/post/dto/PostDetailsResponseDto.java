package com.example.spring_practice.domain.post.dto;

import com.example.spring_practice.domain.comment.dto.CommentResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class PostDetailsResponseDto {
    @Schema(description = "게시물 이미지", example = "https://sample.com")
    private String imgUrl;
    @Schema(description = "게시물 내용", example = "소고기가 먹고싶은데 돈이없습니다. 돈좀 주세요!")
    private String content;
    @Schema(description = "내가 작성자인지 여부", example = "true")
    private boolean isMine;
    //@Schema(description = "댓글 목록")
    //private List<CommentResponseDto> comments;
}
