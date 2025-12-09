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
    static public PostSummaryResponseDto toPostSummaryResponseDto(Post post,String imgUrl, boolean isPostLiked){
        return new PostSummaryResponseDto(
                post.getPostId(),
                post.getTitle(),
                post.getCreatedAt(),
                post.getMember().getNickname(),
                imgUrl,
                post.getPostLikeList().size(),
                post.getViewCount(),
                post.getCommentList().size(),
                isPostLiked
        );
    }
    static public PostDetailsResponseDto toPostDetailsResponseDto(Post post, String imgUrl, Long currentMemberId){
        return new PostDetailsResponseDto(
                imgUrl,
                post.getContent(),
                post.getMember().getMemberId().equals(currentMemberId)
        );
    }
    static public PostResponseDto toPostResponseDto(Post post, String imgUrl, Long currentMemberId, boolean isPostLiked){
        return new PostResponseDto(
                toPostSummaryResponseDto(post, imgUrl, isPostLiked),
                toPostDetailsResponseDto(post, imgUrl, currentMemberId)
        );
    }

    static public PostIdResponseDto toPostIdResponseDto(Long postId){
        return new PostIdResponseDto(postId);
    }
}
