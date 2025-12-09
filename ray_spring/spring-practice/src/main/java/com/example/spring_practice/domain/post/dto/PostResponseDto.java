package com.example.spring_practice.domain.post.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostResponseDto {
    private PostSummaryResponseDto postSummary;
    private PostDetailsResponseDto postDetails;
}
