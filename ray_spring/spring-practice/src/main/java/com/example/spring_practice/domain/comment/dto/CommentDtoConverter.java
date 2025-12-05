package com.example.spring_practice.domain.comment.dto;

import com.example.spring_practice.domain.comment.entity.Comment;
import com.example.spring_practice.domain.shared.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.awt.*;

@Component
@RequiredArgsConstructor
public class CommentDtoConverter {
    static public CommentResponseDto toCommentResponseDto(Comment comment, String imgUrl, Long currentMemberId){
        return new CommentResponseDto(
                comment.getCommentId(),
                comment.getMember().getNickname(),
                imgUrl,
                comment.getCreatedAt(),
                comment.getContent(),
                comment.getMember().getMemberId().equals(currentMemberId));
    }

    static public CommentIdResponseDto toCommentIdResponseDto(Long commentId){
        return new CommentIdResponseDto(commentId);
    }
}
