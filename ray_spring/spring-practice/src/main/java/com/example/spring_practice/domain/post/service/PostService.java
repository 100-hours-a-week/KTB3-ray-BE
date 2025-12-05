package com.example.spring_practice.domain.post.service;

import com.example.spring_practice.domain.member.entity.Member;
import com.example.spring_practice.domain.post.dto.*;
import com.example.spring_practice.domain.post.entity.Post;
import com.example.spring_practice.domain.post.entity.PostLike;
import com.example.spring_practice.domain.post.repository.PostLikeRepository;
import com.example.spring_practice.domain.post.repository.PostRepository;
import com.example.spring_practice.domain.shared.ImageService;
import com.example.spring_practice.global.response.CustomException;
import com.example.spring_practice.global.response.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final ImageService imageService;
    private final PostLikeRepository postLikeRepository;

    @Transactional(readOnly = true)
    public List<PostSummaryResponseDto> getPostList(Long currentMemberId) {
        List<Post> posts = postRepository.findAllWithMember();
        List<PostSummaryResponseDto> postSummaryResponseDtos = new ArrayList<>();
        for (Post post : posts) {
            boolean isPostLiked = postLikeRepository.existsByPost_PostIdAndMember_MemberId(post.getPostId(), currentMemberId);
            postSummaryResponseDtos.add(PostDtoConverter.toPostSummaryResponseDto(post, imageService.getFullImgUrl(post.getImgUrl()), isPostLiked));
        }
        return postSummaryResponseDtos;
    }

    @Transactional
    public PostResponseDto getPostDetail(Long postId, Long currentMemberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        post.increaseViewCount();
        boolean isPostLiked = postLikeRepository.existsByPost_PostIdAndMember_MemberId(postId, currentMemberId);
        return PostDtoConverter.toPostResponseDto(post, imageService.getFullImgUrl(post.getImgUrl()), currentMemberId, isPostLiked);
    }

    @Transactional
    public PostIdResponseDto createPost(PostRequestDto postRequestDto, Member currentMember) {
        Post post = new Post(postRequestDto, currentMember);
        
        if(postRequestDto.getPostImage() != null){
            post.updateImageUrl(imageService.saveImg(postRequestDto.getPostImage()));
        }
        return PostDtoConverter.toPostIdResponseDto(postRepository.save(post).getPostId());
    }

    @Transactional
    public PostIdResponseDto editPost(Long postId, PostRequestDto postRequestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        post.updateTitle(postRequestDto.getTitle());
        post.updateContent(postRequestDto.getContent());
        if(postRequestDto.getPostImage() != null){
            post.updateImageUrl(imageService.saveImg(postRequestDto.getPostImage()));
        }
        return PostDtoConverter.toPostIdResponseDto(post.getPostId());
    }

    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        postRepository.delete(post);
    }

    public void createPostLike(Long postId, Member currentMember) {
        if(!postLikeRepository.existsByPost_PostIdAndMember_MemberId(postId, currentMember.getMemberId())){
            Post post = postRepository.findById(postId).orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
            PostLike postLike = new PostLike(currentMember, post);
            postLikeRepository.save(postLike);
        }
    }

    @Transactional
    public void deletePostLike(Long postId, Long memberId) {
        postLikeRepository.deleteByPost_PostIdAndMember_MemberId(postId, memberId);
    }
}
