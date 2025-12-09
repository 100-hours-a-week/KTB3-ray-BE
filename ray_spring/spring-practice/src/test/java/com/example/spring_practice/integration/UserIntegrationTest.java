package com.example.spring_practice.integration;

import com.example.spring_practice.domain.member.controller.AuthController;
import com.example.spring_practice.domain.member.dto.EditPasswordRequestDto;
import com.example.spring_practice.domain.member.dto.LoginRequestDto;
import com.example.spring_practice.domain.member.entity.Member;
import com.example.spring_practice.domain.member.repository.MemberRepository;
import com.example.spring_practice.domain.member.service.AuthService;
import com.example.spring_practice.global.security.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EntityManager em;

    @BeforeEach
    public void setup() {
        // 회원가입한 유저
        Member member = Member.builder()
                .email("test@test.com")
                .nickname("test")
                .password(passwordEncoder.encode("Asdf1234@"))
                .profileImgUrl("testUrl").build();
        memberRepository.save(member);
    }

    @Test
    void 회원가입_프로필_이미지_없음_성공_201() throws Exception {
        // given
        String email = "signup@test.com";
        String password = "Asdf1234@";
        String nickname = "signup";
        // when // then
        mockMvc.perform(multipart("/auth/signup")
                        .param("email", email)
                        .param("password", password)
                        .param("nickname", nickname))
                .andExpect(status().isCreated());

        Optional<Member> member = memberRepository.findByEmail(email);
        assertThat(member.isPresent()).isTrue();
        assertThat(member.get().getNickname()).isEqualTo(nickname);
        assertThat(member.get().getProfileImgUrl()).isNull();
    }

    @Test
    void 회원가입_프로필_이미지_있음_성공_201() throws Exception {
        // given
        MockMultipartFile profileImage = new MockMultipartFile(
                "profileImage",
                "profile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image content".getBytes()
        );
        String email = "signup@test.com";
        String password = "Asdf1234@";
        String nickname = "signup";
        // when then
        mockMvc.perform(multipart("/auth/signup")
                        .file(profileImage)
                        .param("email", email)
                        .param("password", password)
                        .param("nickname", nickname))
                .andExpect(status().isCreated());

        Optional<Member> member = memberRepository.findByEmail(email);
        assertThat(member.isPresent()).isTrue();
        assertThat(member.get().getNickname()).isEqualTo(nickname);
        assertThat(member.get().getProfileImgUrl()).isNotNull();
    }

    @Test
    void 회원가입_중복된_이메일_실패_409() throws Exception {
        // given
        String email = "signup@test.com";
        String password = "Asdf1234@";
        String nickname = "test";
        // when then
        mockMvc.perform(multipart("/auth/signup")
                        .param("email", email)
                        .param("password", password)
                        .param("nickname", nickname))
                .andExpect(status().isConflict());
    }
    @Test
    void 회원가입_중복된_닉네임_실패_409() throws Exception {
        // given
        String email = "test@test.com";
        String password = "Asdf1234@";
        String nickname = "new";
        // when then
        mockMvc.perform(multipart("/auth/signup")
                        .param("email", email)
                        .param("password", password)
                        .param("nickname", nickname))
                .andExpect(status().isConflict());
    }

    @Test
    void 로그인_성공_200() throws Exception {
        // given

        // when
        String loginContent = objectMapper.writeValueAsString(
                LoginRequestDto.builder()
                        .email("test@test.com")
                        .password("Asdf1234@")
                        .build()
        );

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .content(loginContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        // then
        String responseBody = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(responseBody);

        String accessToken = rootNode.get("data").get("accessToken").asText();

        assertNotNull(accessToken);
        assertTrue(jwtUtil.validateToken(accessToken));
    }

    @Test
    void 로그인_실패_401() throws Exception {
        // given
        String content = objectMapper.writeValueAsString(LoginRequestDto.builder()
                .email("noUser@test")
                .password("Asdf1234@").build());
        // when then
        mockMvc.perform(post("/auth/login").content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

    }

    @Test
    @WithMockUser("test@test.com")
    void 프로필_조회_성공_200() throws Exception {
        // given

        // when // then
        mockMvc.perform(get("/users/my-profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andExpect(jsonPath("$.data.nickname").value("test"));
    }

    @Test
    @WithMockUser("test@test.com")
    void 비밀번호_변경_성공_200() throws Exception {
        // given
        String newPassword = "Asdf1234!";
        String content = objectMapper.writeValueAsString(EditPasswordRequestDto.builder().password(newPassword).build());
        // when // then
        mockMvc.perform(patch("/users/password").content(content).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertThat(passwordEncoder.matches(newPassword, memberRepository.findByEmail("test@test.com").get().getPassword())).isTrue();
    }

    @Test
    @WithMockUser("test@test.com")
    void 프로필_수정_이미지_성공_201() throws Exception {
        // given
        MockMultipartFile newProfileImage = new MockMultipartFile(
                "profileImage",
                "newProfile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image content".getBytes()
        );

        // when // then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/users/profile")
                        .file(newProfileImage))
                .andExpect(status().isOk());

        Optional<Member> member = memberRepository.findByEmail("test@test.com");
        assertThat(member.isPresent()).isTrue();
        assertThat(member.get().getProfileImgUrl()).isNotEqualTo("testUrl");
        assertThat(member.get().getNickname()).isEqualTo("test");
    }
    @Test
    @WithMockUser("test@test.com")
    void 프로필_수정_닉네임_성공_201() throws Exception {
        // given

        // when // then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/users/profile")
                        .param("nickname", "newNickname"))
                .andExpect(status().isOk());

        Optional<Member> member = memberRepository.findByEmail("test@test.com");
        assertThat(member.get().getProfileImgUrl()).isEqualTo("testUrl");
        assertThat(member.get().getNickname()).isEqualTo("newNickname");
    }
    @Test
    @WithMockUser("test@test.com")
    void 프로필_수정_이미지_닉네임_성공_201() throws Exception {
        // given
        MockMultipartFile newProfileImage = new MockMultipartFile(
                "profileImage",
                "newProfile.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image content".getBytes()
        );

        // when // then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/users/profile")
                        .file(newProfileImage)
                        .param("nickname", "newNickname"))
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        Optional<Member> member = memberRepository.findByEmail("test@test.com");
        assertThat(member.isPresent()).isTrue();
        assertThat(member.get().getProfileImgUrl()).isNotEqualTo("testUrl");
        assertThat(member.get().getNickname()).isEqualTo("newNickname");
    }

    @Test
    void 이메일_중복체크_사용가능_성공_200() throws Exception {
        // given
        String uniqueEmail = "unique@email.com";

        // when then
        mockMvc.perform(get("/users/email/duplicate-check?email="+uniqueEmail).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duplicated").value(false));
    }
    @Test
    void 이메일_중복체크_사용불가능_성공_200() throws Exception {
        // given
        String duplicatedEmail = "test@test.com";

        // when then
        mockMvc.perform(get("/users/email/duplicate-check?email="+duplicatedEmail).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duplicated").value(true));
    }
    @Test
    void 닉네임_중복체크_사용가능_성공_200() throws Exception {
        // given
        String uniqueNickname = "unique";

        // when then
        mockMvc.perform(get("/users/nickname/duplicate-check?nickname="+ uniqueNickname).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duplicated").value(false));
    }
    @Test
    void 닉네임_중복체크_사용불가능_성공_200() throws Exception {
        // given
        String duplicatedNickname = "test";

        // when then
        mockMvc.perform(get("/users/nickname/duplicate-check?nickname="+ duplicatedNickname).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duplicated").value(true));
    }

}

