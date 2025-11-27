package com.example.spring_practice.global.security;

import com.example.spring_practice.domain.comment.entity.Comment;
import com.example.spring_practice.domain.comment.repository.CommentRepository;
import com.example.spring_practice.domain.member.repository.MemberRepository;
import com.example.spring_practice.domain.member.service.AuthService;
import com.example.spring_practice.domain.member.service.MemberService;
import com.example.spring_practice.domain.post.entity.Post;
import com.example.spring_practice.domain.post.entity.PostLike;
import com.example.spring_practice.domain.post.repository.PostLikeRepository;
import com.example.spring_practice.domain.post.repository.PostRepository;
import com.example.spring_practice.global.response.CustomException;
import com.example.spring_practice.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component("authorizationChecker")
public class AuthorizationChecker {
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final AuthService authService;
    private final PostLikeRepository postLikeRepository;

    public boolean isPostAuthor(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if(!post.getMember().getMemberId().equals(authService.getCurrentMember().getMemberId())) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
        return true;
    }

    public boolean isCommentAuthor(Long commentId) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if(!comment.getMember().getMemberId().equals(authService.getCurrentMember().getMemberId())) {
            throw new CustomException(ErrorCode.NO_PERMISSION);
        }
        return true;
    }
}
