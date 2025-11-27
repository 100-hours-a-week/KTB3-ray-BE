package com.example.spring_practice.domain.post.dto;

import com.example.spring_practice.domain.comment.dto.CommentDtoConverter;
import com.example.spring_practice.domain.comment.dto.CommentResponseDto;
import com.example.spring_practice.domain.comment.entity.Comment;
import com.example.spring_practice.domain.post.entity.Post;
import com.example.spring_practice.domain.shared.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class PostDtoConverter {
    private final ImageService imageService;
    public PostSummaryResponseDto toPostSummaryResponseDto(Post post,boolean isPostLiked){
        return new PostSummaryResponseDto(
                post.getPostId(),
                post.getTitle(),
                post.getCreatedAt(),
                post.getMember().getNickname(),
                imageService.getFullImgUrl(post.getMember().getProfileImgUrl()),
                post.getPostLikeList().size(),
                post.getViewCount(),
                post.getCommentList().size(),
                isPostLiked
        );
    }
    public PostDetailsResponseDto toPostDetailsResponseDto(Post post, Long currentMemberId){
        return new PostDetailsResponseDto(
                imageService.getFullImgUrl(post.getImgUrl()),
                post.getContent(),
                post.getMember().getMemberId().equals(currentMemberId)
        );
    }
    public PostResponseDto toPostResponseDto(Post post, Long currentMemberId, boolean isPostLiked){
        return new PostResponseDto(
                toPostSummaryResponseDto(post, isPostLiked),
                toPostDetailsResponseDto(post, currentMemberId)
        );
    }

    public PostIdResponseDto toPostIdResponseDto(Long postId){
        return new PostIdResponseDto(postId);
    }
}
