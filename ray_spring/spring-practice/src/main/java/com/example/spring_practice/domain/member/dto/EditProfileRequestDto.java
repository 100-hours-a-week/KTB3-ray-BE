package com.example.spring_practice.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditProfileRequestDto {
    @Schema(description = "변경할 닉네임", example = "김초코", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String nickname;
    @Schema(description = "변경할 이미지 파일", type = "String", format = "binary", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private MultipartFile profileImage;
}
