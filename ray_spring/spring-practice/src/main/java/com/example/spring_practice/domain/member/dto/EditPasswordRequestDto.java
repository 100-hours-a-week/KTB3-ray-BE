package com.example.spring_practice.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditPasswordRequestDto {
    @Schema(description = "변경할 비밀번호", example = "Asdf1234@", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
