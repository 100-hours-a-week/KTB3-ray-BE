package com.example.spring_practice.integration;

import com.example.spring_practice.domain.member.entity.Member;
import com.example.spring_practice.domain.member.repository.MemberRepository;
import com.example.spring_practice.domain.post.dto.PostRequestDto;
import com.example.spring_practice.domain.post.dto.PostResponseDto;
import com.example.spring_practice.domain.post.dto.PostSummaryResponseDto;
import com.example.spring_practice.domain.post.entity.Post;
import com.example.spring_practice.domain.post.entity.PostLike;
import com.example.spring_practice.domain.post.repository.PostLikeRepository;
import com.example.spring_practice.domain.post.repository.PostRepository;
import com.example.spring_practice.domain.shared.ImageService;
import com.example.spring_practice.global.response.ApiResponse;
import com.fasterxml.jackson.core.type.TypeReference;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
//@Transactional
@ActiveProfiles("test")
public class PostIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ImageService imageService;
    @Autowired
    private PostLikeRepository postLikeRepository;
    @Autowired
    private EntityManager em;

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

        // 게시글 1 - 유저 1
        Post post1 = Post.builder()
                .title("title1")
                .content("content1")
                .imgUrl("testUrl1")
                .member(savedMember1).build();
        //Post post1 = new Post(new PostRequestDto("title1", "content1", null), savedMember1);
        postRepository.save(post1);
        // 게시글 2 - 유저 2
        Post post2 = Post.builder()
                .title("title2")
                .content("content2")
                .imgUrl("testUrl2")
                .member(savedMember2).build();
        //Post post2 = new Post(new PostRequestDto("title2", "content2", null), savedMember2);
        postRepository.save(post2);


        em.flush();
        em.clear();
        assertThat(postRepository.findAll().size()).isEqualTo(2);
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글_목록_불러오기_성공_200() throws Exception {
        // given
        PostLike postLike = PostLike.builder()
                .member(memberRepository.findByEmail("post1@test.com").get())
                .post(postRepository.findAll().get(1)).build();
        postLikeRepository.save(postLike);

        // when
        MvcResult result = mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andReturn();
        // then
        String responseBody = result.getResponse().getContentAsString();

        ApiResponse<List<PostSummaryResponseDto>> response = objectMapper.readValue(
                responseBody,
                new TypeReference<ApiResponse<List<PostSummaryResponseDto>>>() {}
        );
        List<PostSummaryResponseDto> postSummaryList = response.getData();
        PostSummaryResponseDto postSummary1 = postSummaryList.get(0);
        PostSummaryResponseDto postSummary2 = postSummaryList.get(1);
        assertThat(postSummaryList.size()).isEqualTo(2);
        assertThat(postSummary1.getTitle()).isEqualTo("title1");
        assertThat(postSummary2.getTitle()).isEqualTo("title2");
        assertThat(postSummary1.isPostLiked()).isFalse();
        assertThat(postSummary2.isPostLiked()).isTrue();
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글_상세보기_성공_200() throws Exception {
        // given
        Long postId = postRepository.findAll().get(0).getPostId();

        // when // then
        MvcResult result = mockMvc.perform(get("/posts/"+postId))
                .andExpect(status().isOk())
                .andReturn();
        String responseBody = result.getResponse().getContentAsString();

        ApiResponse<PostResponseDto> response = objectMapper.readValue(
                responseBody,
                new TypeReference<ApiResponse<PostResponseDto>>() {}
        );
        PostResponseDto postResponseDto = response.getData();
        assertThat(postResponseDto.getPostSummary().getTitle()).isEqualTo("title1");
        assertThat(postResponseDto.getPostDetails().getContent()).isEqualTo("content1");
        assertThat(postResponseDto.getPostDetails().isMine()).isTrue();
        assertThat(postResponseDto.getPostSummary().getViewCount()).isEqualTo(1);
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글_상세보기_없음_실패_404() throws Exception {
        Long postId = -1L;
        mockMvc.perform(get("/posts/"+postId))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글작성_이미지없음_성공_201() throws Exception {
        // given
        String title = "test title";
        String content = "test content";
        // when
        MvcResult result = mockMvc.perform(multipart("/posts")
                        .param("title", title)
                        .param("content", content))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(responseBody);

        Long postId = rootNode.get("data").get("postId").asLong();
        Optional<Post> post = postRepository.findById(postId);
        assertThat(post).isPresent();
        assertThat(post.get().getTitle()).isEqualTo(title);
        assertThat(post.get().getContent()).isEqualTo(content);
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글작성_이미지있음_성공_201() throws Exception {
        // given
        MockMultipartFile profileImage = new MockMultipartFile(
                "postImage",
                "postImage.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image content".getBytes()
        );
        String title = "test title";
        String content = "test content";

        // when
        MvcResult result = mockMvc.perform(multipart("/posts")
                        .file(profileImage)
                        .param("title", title)
                        .param("content", content))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        JsonNode rootNode = objectMapper.readTree(responseBody);

        Long postId = rootNode.get("data").get("postId").asLong();
        Optional<Post> post = postRepository.findById(postId);
        assertThat(post).isPresent();
        assertThat(post.get().getTitle()).isEqualTo(title);
        assertThat(post.get().getContent()).isEqualTo(content);
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글수정_이미지_수정_성공_200() throws Exception {
        // given
        MockMultipartFile newPostImage = new MockMultipartFile(
                "postImage",
                "newPostImage.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image content".getBytes()
        );
        Post originalPost = postRepository.findAll().get(0);
        Long postId = originalPost.getPostId();
        // when // then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/posts/"+postId)
                .file(newPostImage)
                .param("title", "title1")
                .param("content", "content1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.postId").value(postId));
        Optional<Post> newPost = postRepository.findById(postId);
        assertThat(newPost.get().getTitle()).isEqualTo("title1");
        assertThat(newPost.get().getContent()).isEqualTo("content1");
        assertThat(newPost.get().getImgUrl()).isNotEqualTo("testUrl1");
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글수정_글_내용_수정_성공_200() throws Exception {
        // given
        Post originalPost = postRepository.findAll().get(0);
        Long postId = originalPost.getPostId();
        // when // then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/posts/"+postId)
                        .param("title", "newTitle")
                        .param("content", "newContent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.postId").value(postId));
        Optional<Post> newPost = postRepository.findById(postId);
        assertThat(newPost.get().getTitle()).isEqualTo("newTitle");
        assertThat(newPost.get().getContent()).isEqualTo("newContent");
        assertThat(newPost.get().getImgUrl()).isEqualTo("testUrl1");
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글수정_이미지_글_내용_수정_성공_200() throws Exception {
        // given
        MockMultipartFile newPostImage = new MockMultipartFile(
                "postImage",
                "newPostImage.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image content".getBytes()
        );
        Post originalPost = postRepository.findAll().get(0);
        Long postId = originalPost.getPostId();
        // when // then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/posts/"+postId)
                        .file(newPostImage)
                        .param("title", "newTitle")
                        .param("content", "newContent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.postId").value(postId));
        Optional<Post> newPost = postRepository.findById(postId);
        assertThat(newPost.get().getTitle()).isEqualTo("newTitle");
        assertThat(newPost.get().getContent()).isEqualTo("newContent");
        assertThat(newPost.get().getImgUrl()).isNotEqualTo("testUrl1");
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글수정_권한없음_실패_403() throws Exception {
        // given
        Post notMyPost = postRepository.findAll().get(1);
        Long postId = notMyPost.getPostId();
        String originalPostImgUrl = notMyPost.getImgUrl();
        String originalTitle = notMyPost.getTitle();
        String originalContent = notMyPost.getContent();
        // when // then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/posts/"+postId)
                        .param("title", "newTitle")
                        .param("content", "newContent"))
                .andExpect(status().isForbidden());

        assertThat(notMyPost.getImgUrl()).isEqualTo(originalPostImgUrl);
        assertThat(notMyPost.getTitle()).isEqualTo(originalTitle);
        assertThat(notMyPost.getContent()).isEqualTo(originalContent);
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글삭제_성공_200() throws Exception {
        // given
        Post myPost = postRepository.findAll().get(0);
        Long postId = myPost.getPostId();
        // when
        // then
        mockMvc.perform(delete("/posts/"+postId))
                .andExpect(status().isOk());
        assertThat(postRepository.findById(postId)).isNotPresent();
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글삭제_권한없음_실패_403()throws Exception {
        // given
        Post notMyPost = postRepository.findAll().get(1);
        Long postId = notMyPost.getPostId();
        // when
        // then
        mockMvc.perform(delete("/posts/"+postId))
                .andExpect(status().isForbidden());
        assertThat(postRepository.findById(postId)).isPresent();
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글_좋아요_성공_200() throws Exception {
        // given
        Post post = postRepository.findAll().get(0);
        Long postId = post.getPostId();
        // when // then
        mockMvc.perform(post("/posts/"+postId+"/like"))
                .andExpect(status().isOk());
        assertThat(postLikeRepository.findByPost_PostIdAndMember_MemberId(postId,
                        memberRepository.findByEmail("post1@test.com").get().getMemberId())).isPresent();

    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글_좋아요_글없음_실패_404() throws Exception {
        // given
        Long invalidPostId = -1L;
        // when
        // then
        mockMvc.perform(post("/posts/"+invalidPostId+"/like"))
                .andExpect(status().isNotFound());
        assertThat(postLikeRepository.findById(invalidPostId)).isNotPresent();
        assertThat(postLikeRepository.findByPost_PostIdAndMember_MemberId(invalidPostId,
                memberRepository.findByEmail("post1@test.com").get().getMemberId())).isNotPresent();
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글_좋아요_중복_실패_409() throws Exception {
        // given
        Post post = postRepository.findAll().get(0);
        Long postId = post.getPostId();
        PostLike postLike = PostLike.builder()
                .member(memberRepository.findByEmail("post1@test.com").get())
                .post(post).build();
        postLikeRepository.save(postLike);
        // when
        // then
        mockMvc.perform(post("/posts/"+postId+"/like"))
                .andExpect(status().isConflict());
        assertThat(postLikeRepository.findByPost_PostIdAndMember_MemberId(postId,
                memberRepository.findByEmail("post1@test.com").get().getMemberId())).isPresent();
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글_좋아요_취소_성공_200() throws Exception {
        // given
        Post post = postRepository.findAll().get(0);
        Long postId = post.getPostId();
        PostLike postLike = PostLike.builder()
                .member(memberRepository.findByEmail("post1@test.com").get())
                .post(post).build();
        postLikeRepository.save(postLike);
        // when
        // then
        mockMvc.perform(delete("/posts/"+postId+"/like"))
                .andExpect(status().isOk());
        assertThat(postLikeRepository.findByPost_PostIdAndMember_MemberId(postId,
                memberRepository.findByEmail("post1@test.com").get().getMemberId())).isNotPresent();
    }

    @Test
    @WithMockUser("post1@test.com")
    @Transactional
    void 글_좋아요_취소_없음_실패_404() throws Exception {
        // given
        Post post = postRepository.findAll().get(0);
        Long postId = post.getPostId();
        // when
        // then
        mockMvc.perform(delete("/posts/"+postId+"/like"))
                .andExpect(status().isNotFound());
        assertThat(postLikeRepository.findByPost_PostIdAndMember_MemberId(postId,
                memberRepository.findByEmail("post1@test.com").get().getMemberId())).isNotPresent();
    }

}

