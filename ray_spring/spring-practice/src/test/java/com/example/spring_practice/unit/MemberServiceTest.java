package com.example.spring_practice.unit;

import com.example.spring_practice.domain.member.dto.*;
import com.example.spring_practice.domain.member.entity.Member;
import com.example.spring_practice.domain.member.repository.MemberRepository;
import com.example.spring_practice.domain.member.service.AuthService;
import com.example.spring_practice.domain.member.service.MemberService;
import com.example.spring_practice.domain.shared.ImageService;
import com.example.spring_practice.global.response.CustomException;
import com.example.spring_practice.global.response.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private ImageService imageService;
    @Mock
    private AuthService authService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private MemberService memberService;


    @Test
    void 회원가입_이미지_없음_성공(){
        // Given
        when(memberRepository.existsByEmail(any())).thenReturn(false);
        when(memberRepository.existsByNickname(any())).thenReturn(false);
        when(memberRepository.save(any())).thenReturn(new Member("","",""));

        // When
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@gmail.com")
                .password("Asdf1234@")
                .nickname("asdf")
                .build();

        memberService.signUp(signUpRequestDto);

        // Then
        verify(memberRepository, times(1)).save(any());
    }

    @Test
    void 회원가입_이미지_있음_성공(){
        // Given
        when(memberRepository.existsByEmail(any())).thenReturn(false);
        when(memberRepository.existsByNickname(any())).thenReturn(false);
        when(memberRepository.save(any())).thenReturn(new Member("","",""));
        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image content".getBytes()
        );
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@gmail.com")
                .password("Asdf1234@")
                .nickname("asdf")
                .profileImage(profileImage)
                .build();

        // When
        memberService.signUp(signUpRequestDto);

        // Then
        verify(memberRepository, times(1)).save(any());
    }

    @Test
    void 회원가입_이메일_중복_실패(){
        // Given
        when(memberRepository.existsByEmail(any())).thenReturn(true);

        // When
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@gmail.com")
                .password("Asdf1234@")
                .nickname("asdf")
                .build();

        // Then
        assertThatThrownBy(() -> memberService.signUp(signUpRequestDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
    }

    @Test
    void 회원가입_닉네임_중복_실패(){
        // Given
        when(memberRepository.existsByEmail(any())).thenReturn(false);
        when(memberRepository.existsByNickname(any())).thenReturn(true);

        // When
        SignUpRequestDto signUpRequestDto = SignUpRequestDto.builder()
                .email("test@gmail.com")
                .password("Asdf1234@")
                .nickname("asdf")
                .build();

        // Then
        assertThatThrownBy(() -> memberService.signUp(signUpRequestDto))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_NICKNAME);
    }

    @Test
    void 프로필_불러오기_성공(){
        // given
        String email = "test@test.com";
        String nickname = "테스트유저";
        String profileImgUrl = "testImg";
        String fullProfileImgUrl = "https://testImg";
        Member member = Member.builder()
                .email(email)
                .nickname(nickname)
                .profileImgUrl(profileImgUrl)
                .build();

        when(authService.getCurrentMember()).thenReturn(member);
        when(imageService.getFullImgUrl(member.getProfileImgUrl())).thenReturn(fullProfileImgUrl);
        // when
        ProfileResponseDto profileResponseDto = memberService.getMyProfile();
        // then
        assertThat(profileResponseDto.getEmail()).isEqualTo(email);
        assertThat(profileResponseDto.getNickname()).isEqualTo(nickname);
        assertThat(profileResponseDto.getProfileImage()).isEqualTo(fullProfileImgUrl);
    }

    @Test
    void 비밀번호_변경_성공() {
        // given
        Member member = Member.builder()
                .email("test@test.com")
                .password("oldEncodedPassword")
                .nickname("테스터")
                .build();

        when(authService.getCurrentMember()).thenReturn(member);
        when(passwordEncoder.encode("Asdf1234!")).thenReturn("newEncodedPassword");

        EditPasswordRequestDto request = EditPasswordRequestDto.builder()
                .password("Asdf1234!")
                .build();

        // when
        memberService.editPassword(request);

        // then
        verify(authService, times(1)).getCurrentMember();
        verify(passwordEncoder, times(1)).encode("Asdf1234!");
        assertEquals("newEncodedPassword", member.getPassword());
    }

    @Test
    void 프로필_이미지_변경_성공(){
        // given
        String email = "test@test.com";
        String originalNickname = "테스트유저";
        String originalProfileImgUrl = "https://original/profile/img";

        Member member = Member.builder()
                .email(email)
                .nickname(originalNickname)
                .profileImgUrl(originalProfileImgUrl)
                .build();
        when(authService.getCurrentMember()).thenReturn(member);

        MockMultipartFile newProfileImage = new MockMultipartFile(
                "profileImage",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image content".getBytes()
        );
        String newProfileImgUrl = "https://new/profile/img";
        when(imageService.saveImg(newProfileImage)).thenReturn(newProfileImgUrl);

        EditProfileRequestDto editProfileRequestDto = EditProfileRequestDto.builder()
                .nickname(null)
                .profileImage(newProfileImage)
                .build();

        // when
        memberService.editProfile(editProfileRequestDto);

        // then
        assertThat(member.getProfileImgUrl()).isEqualTo(newProfileImgUrl);
        assertThat(member.getNickname()).isEqualTo(originalNickname);

    }
    @Test
    void 프로필_닉네임_변경_성공(){
        // given
        String email = "test@test.com";
        String originalNickname = "테스트유저";
        String originalProfileImgUrl = "https://test/url";
        Member member = Member.builder()
                .email(email)
                .nickname(originalNickname)
                .profileImgUrl(originalProfileImgUrl)
                .build();
        when(authService.getCurrentMember()).thenReturn(member);

        String newNickName = "새로운닉네임";
        EditProfileRequestDto editProfileRequestDto = EditProfileRequestDto.builder()
                .nickname(newNickName)
                .profileImage(null).build();

        // when
        memberService.editProfile(editProfileRequestDto);

        // then
        assertThat(member.getNickname()).isEqualTo(newNickName);
        assertThat(member.getProfileImgUrl()).isEqualTo(originalProfileImgUrl);

    }
    @Test
    void 프로필_이미지_닉네임_변경_성공(){
        // given
        String email = "test@test.com";
        String originalNickname = "테스트유저";
        String originalProfileImgUrl = "https://test/url";
        Member member = Member.builder()
                .email(email)
                .nickname(originalNickname)
                .profileImgUrl(originalProfileImgUrl)
                .build();
        when(authService.getCurrentMember()).thenReturn(member);

        String newNickName = "새로운닉네임";

        MockMultipartFile newProfileImage = new MockMultipartFile(
                "profileImage",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image content".getBytes()
        );
        String newProfileImgUrl = "https://new/profile/img";
        when(imageService.saveImg(newProfileImage)).thenReturn(newProfileImgUrl);

        EditProfileRequestDto editProfileRequestDto = EditProfileRequestDto.builder()
                .nickname(newNickName)
                .profileImage(newProfileImage)
                .build();

        // when
        memberService.editProfile(editProfileRequestDto);

        // then
        assertThat(member.getNickname()).isEqualTo(newNickName);
        assertThat(member.getProfileImgUrl()).isEqualTo(newProfileImgUrl);
    }

    @Test
    void 이메일_중복확인_사용가능_성공(){
        // given
        String email = "test@gmail.com";
        when(memberRepository.existsByEmail(email)).thenReturn(false);
        //when(memberDtoConverter.toDuplicateCheckResponseDto(false)).thenReturn(new DuplicateCheckResponseDto(false));

        // when & then
        assertThat(memberService.emailDuplicateCheck(email).isDuplicated()).isFalse();
    }
    @Test
    void 이메일_중복확인_사용불가능_성공(){
        // given
        String email = "test@gmail.com";
        when(memberRepository.existsByEmail(email)).thenReturn(true);
        //when(memberDtoConverter.toDuplicateCheckResponseDto(true)).thenReturn(new DuplicateCheckResponseDto(true));

        // when & then
        assertThat(memberService.emailDuplicateCheck(email).isDuplicated()).isTrue();
    }

    @Test
    void 닉네임_중복확인_사용가능_성공(){
        // given
        String nickname = "unique";
        when(memberRepository.existsByNickname(nickname)).thenReturn(false);
        //when(memberDtoConverter.toDuplicateCheckResponseDto(false)).thenReturn(new DuplicateCheckResponseDto(false));

        // when & then
        assertThat(memberService.nicknameDuplicateCheck(nickname).isDuplicated()).isFalse();
    }
    @Test
    void 닉네임_중복확인_사용불가능_성공(){
        // given
        String nickname = "common";
        when(memberRepository.existsByNickname(nickname)).thenReturn(true);
        //when(memberDtoConverter.toDuplicateCheckResponseDto(true)).thenReturn(new DuplicateCheckResponseDto(true));

        // when & then
        assertThat(memberService.nicknameDuplicateCheck(nickname).isDuplicated()).isTrue();
    }
}
