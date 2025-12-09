package com.example.spring_practice.domain.comment.service;

import com.example.spring_practice.domain.comment.dto.CommentDtoConverter;
import com.example.spring_practice.domain.comment.dto.CommentIdResponseDto;
import com.example.spring_practice.domain.comment.dto.CommentRequestDto;
import com.example.spring_practice.domain.comment.dto.CommentResponseDto;
import com.example.spring_practice.domain.comment.entity.Comment;
import com.example.spring_practice.domain.comment.repository.CommentRepository;
import com.example.spring_practice.domain.member.entity.Member;
import com.example.spring_practice.domain.member.repository.MemberRepository;
import com.example.spring_practice.domain.post.entity.Post;
import com.example.spring_practice.domain.post.repository.PostRepository;
import com.example.spring_practice.domain.shared.ImageService;
import com.example.spring_practice.global.response.CustomException;
import com.example.spring_practice.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ImageService imageService;

    @Transactional
    public CommentResponseDto createComment(Long postId, CommentRequestDto commentRequestDto, Long currentMemberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Comment comment = new Comment(commentRequestDto, member, post);
        Comment savedComment = commentRepository.save(comment);

        return CommentDtoConverter.toCommentResponseDto(savedComment, imageService.getFullImgUrl(member.getProfileImgUrl()), currentMemberId);
    }

    @Transactional
    public CommentIdResponseDto updateComment(Long postId, Long commentId, CommentRequestDto dto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        if (!comment.getPost().getPostId().equals(postId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        comment.updateContent(dto.getContent());

        return CommentDtoConverter.toCommentIdResponseDto(comment.getCommentId());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (!comment.getPost().getPostId().equals(postId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }
        commentRepository.delete(comment);
    }

    public List<CommentResponseDto> getComments(Long postId, Long currentMemberId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        List<CommentResponseDto> commentList = new ArrayList<>();
        for (Comment c : post.getCommentList()){
            commentList.add(CommentDtoConverter.toCommentResponseDto(c, imageService.getFullImgUrl(c.getMember().getProfileImgUrl()), currentMemberId));
        }
        return commentList;
    }
}
