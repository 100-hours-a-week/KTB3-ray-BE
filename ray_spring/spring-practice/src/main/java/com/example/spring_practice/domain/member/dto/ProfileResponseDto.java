package com.example.spring_practice.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProfileResponseDto {
    @Schema(description = "이메일", example = "example@gmail.com")
    private String email;
    @Schema(description = "닉네임", example = "김뽀삐")
    private String nickname;
    @Schema(description = "프로필 이미지", example = "https://sample_url")
    private String profileImage;
}
