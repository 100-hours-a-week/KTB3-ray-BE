package com.example.spring_practice.integration;

import com.example.spring_practice.domain.comment.controller.CommentController;
import com.example.spring_practice.domain.comment.dto.CommentRequestDto;
import com.example.spring_practice.domain.comment.dto.CommentResponseDto;
import com.example.spring_practice.domain.comment.entity.Comment;
import com.example.spring_practice.domain.comment.repository.CommentRepository;
import com.example.spring_practice.domain.comment.service.CommentService;
import com.example.spring_practice.domain.member.entity.Member;
import com.example.spring_practice.domain.member.repository.MemberRepository;
import com.example.spring_practice.domain.post.dto.PostResponseDto;
import com.example.spring_practice.domain.post.entity.Post;
import com.example.spring_practice.domain.post.repository.PostRepository;
import com.example.spring_practice.global.response.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class CommentIntegrationTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private EntityManager em;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        // 유저 1
        Member member1 = Member.builder()
                .email("post1@test.com")
                .nickname("test1")
                .password(passwordEncoder.encode("Asdf1234@"))
                .profileImgUrl("testUrl").build();
        Member savedMember1 = memberRepository.save(member1);
        // 유저 2
        Member member2 = Member.builder()
                .email("post2@test.com")
                .nickname("test2")
                .password(passwordEncoder.encode("Asdf1234@"))
                .profileImgUrl("testUrl").build();
        Member savedMember2 = memberRepository.save(member2);

        Post post1 = Post.builder()
                .title("title1")
                .content("content1")
                .imgUrl("testUrl1")
                .member(savedMember1).build();
        Post savedPost1 = postRepository.save(post1);

        Post post2 = Post.builder()
                .title("title2")
                .content("content2")
                .imgUrl("testUrl2")
                .member(savedMember1).build();
        Post savedPost2 = postRepository.save(post2);


        Comment comment1 = Comment.builder()
                .content("test comment1")
                .member(savedMember1)
                .post(savedPost1).build();
        Comment comment2 = Comment.builder()
                .content("test comment2")
                .member(savedMember2)
                .post(savedPost1).build();
        commentRepository.save(comment1);
        commentRepository.save(comment2);
        em.flush();
        em.clear();
    }

    @Test
    @WithMockUser("post1@test.com")
    void 댓글_등록_성공_201() throws Exception {
        // given
        String commentContent = objectMapper.writeValueAsString(
                CommentRequestDto.builder()
                        .content("test create comment").build()
        );
        Long postId = postRepository.findAll().get(0).getPostId();
        // when
        mockMvc.perform(post("/posts/" + postId + "/comments")
                        .content(commentContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

    }
    @Test
    @WithMockUser("post1@test.com")
    void 댓글_등록_실패_게시글_없음_404() throws Exception {
        // given
        String commentContent = objectMapper.writeValueAsString(
                CommentRequestDto.builder()
                        .content("test create comment").build()
        );
        Long postId = -1L;

        // when

        // then
        mockMvc.perform(post("/posts/" + postId + "/comments")
                        .content(commentContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser("post1@test.com")
    void 댓글_불러오기_성공_200() throws Exception {
        // given
        Long postId = postRepository.findAll().get(0).getPostId();
        // when
        MvcResult result = mockMvc.perform(get("/posts/" + postId + "/comments"))
                .andExpect(status().isOk())
                .andReturn();
        // then
        String responseBody = result.getResponse().getContentAsString();
        ApiResponse<List<CommentResponseDto>> response = objectMapper.readValue(
                responseBody,
                new TypeReference<ApiResponse<List<CommentResponseDto>>>() {}
        );
        List<CommentResponseDto> commentResponseDtos = response.getData();
        assertThat(commentResponseDtos.size()).isEqualTo(2);
        assertThat(commentResponseDtos.get(0).getContent()).isEqualTo("test comment1");
        assertThat(commentResponseDtos.get(0).isMine()).isEqualTo(true);
        assertThat(commentResponseDtos.get(1).getContent()).isEqualTo("test comment2");
        assertThat(commentResponseDtos.get(1).isMine()).isEqualTo(false);

    }

    @Test
    @WithMockUser("post1@test.com")
    void 댓글_불러오기_실패_게시글_없음_404() throws Exception {
        // given
        Long postId = -1L;
        // when
        mockMvc.perform(get("/posts/" + postId + "/comments"))
                .andExpect(status().isNotFound());

        // then
    }

    @Test
    @WithMockUser("post1@test.com")
    void 댓글_수정_성공_200() throws Exception {
        // given
        String patchCommentContent = objectMapper.writeValueAsString(
                CommentRequestDto.builder()
                        .content("test patch comment").build()
        );
        Long postId = postRepository.findAll().get(0).getPostId();
        Long commentId = commentRepository.findAll().get(0).getCommentId();
        // when // then
        mockMvc.perform(patch("/posts/" + postId + "/comments/" + commentId)
                        .content(patchCommentContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.commentId").value(commentId));

        assertThat(commentRepository.findById(commentId).get().getContent()).isEqualTo("test patch comment");
    }

    @Test
    @WithMockUser("post1@test.com")
    void 댓글_수정_실패_권한없음_403() throws Exception {
        // given
        String patchCommentContent = objectMapper.writeValueAsString(
                CommentRequestDto.builder()
                        .content("test patch comment").build()
        );
        Long postId = postRepository.findAll().get(0).getPostId();
        Long commentId = commentRepository.findAll().get(1).getCommentId();
        // when
        mockMvc.perform(patch("/posts/" + postId + "/comments/" + commentId)
                        .content(patchCommentContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        // then
    }

    @Test
    @WithMockUser("post1@test.com")
    void 댓글_수정_실패_댓글_없음_404() throws Exception {
        // given
        String patchCommentContent = objectMapper.writeValueAsString(
                CommentRequestDto.builder()
                        .content("test patch comment").build()
        );
        Long postId = postRepository.findAll().get(1).getPostId();
        Long commentId = -1L;

        // when
        mockMvc.perform(patch("/posts/" + postId + "/comments/" + commentId)
                        .content(patchCommentContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        // then
    }

    @Test
    @WithMockUser("post1@test.com")
    void 댓글_수정_실패_게시글_댓글_맞지않음_404() throws Exception {
        // given
        String patchCommentContent = objectMapper.writeValueAsString(
                CommentRequestDto.builder()
                        .content("test patch comment").build()
        );
        Long postId = postRepository.findAll().get(1).getPostId();
        Long commentId = commentRepository.findAll().get(0).getCommentId();

        // when
        mockMvc.perform(patch("/posts/" + postId + "/comments/" + commentId)
                        .content(patchCommentContent)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        // then
    }

    @Test
    @WithMockUser("post1@test.com")
    void 댓글_삭제_성공_200() throws Exception {
        // given
        Long postId = postRepository.findAll().get(0).getPostId();
        Long commentId = commentRepository.findAll().get(0).getCommentId();
        // when
        mockMvc.perform(delete("/posts/" + postId + "/comments/" + commentId))
                .andExpect(status().isOk());
        // then
        assertThat(commentRepository.findById(commentId).isPresent()).isEqualTo(false);
    }

    @Test
    @WithMockUser("post1@test.com")
    void 댓글_삭제_실패_권한_없음_403() throws Exception {
        // given
        Long postId = postRepository.findAll().get(0).getPostId();
        Long commentId = commentRepository.findAll().get(1).getCommentId();
        // when
        mockMvc.perform(delete("/posts/" + postId + "/comments/" + commentId))
                .andExpect(status().isForbidden());
        // then
        assertThat(commentRepository.findById(commentId).isPresent()).isEqualTo(true);
    }

    @Test
    @WithMockUser("post1@test.com")
    void 댓글_삭제_실패_댓글_없음_404() throws Exception {
        // given
        Long postId = postRepository.findAll().get(0).getPostId();
        Long commentId = -1L;
        // when
        mockMvc.perform(delete("/posts/" + postId + "/comments/" + commentId))
                .andExpect(status().isNotFound());
        // then
        assertThat(commentRepository.findById(commentId).isPresent()).isEqualTo(false);
    }

    @Test
    @WithMockUser("post1@test.com")
    void 댓글_삭제_실패_게시글_댓글_맞지않음_404() throws Exception {
        // given
        Long postId = postRepository.findAll().get(1).getPostId();
        Long commentId = commentRepository.findAll().get(0).getCommentId();
        // when
        mockMvc.perform(delete("/posts/" + postId + "/comments/" + commentId))
                .andExpect(status().isNotFound());
        // then
        assertThat(commentRepository.findById(commentId).isPresent()).isEqualTo(true);
    }
}
