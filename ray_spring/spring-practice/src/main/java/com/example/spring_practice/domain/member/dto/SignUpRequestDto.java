package com.example.spring_practice.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequestDto {
    @Schema(description = "이메일", example = "example@gmail.com", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "올바른 이베일 주소 형식을 입력해주세요")
    private String email;
    @Schema(description = "비밀번호", example = "Asdf1234@", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,20}$",
            message = "비밀번호는 8자 이상 20자 이하이며, 대문자, 소문자, 숫자, 특수문자를 각각 최소 1개 포함해야 합니다"
    )
    private String password;
    @Schema(description = "닉네임", example = "김뽀삐", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "닉네임을 입력해주세요")
    @Size(max = 10, message = "닉네임은 최대 10자 까지 작성 가능합니다")
    @Pattern(regexp = "^\\S+$", message = "띄어쓰기를 없애주세요")
    private String nickname;
    @Schema(description = "프로필 이미지", type = "String", format = "binary", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private MultipartFile profileImage;
}
