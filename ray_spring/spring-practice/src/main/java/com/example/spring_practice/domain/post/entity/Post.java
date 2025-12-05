package com.example.spring_practice.domain.post.entity;

import com.example.spring_practice.domain.comment.entity.Comment;
import com.example.spring_practice.domain.member.entity.Member;
import com.example.spring_practice.domain.post.dto.PostRequestDto;
import com.example.spring_practice.global.response.CustomException;
import com.example.spring_practice.global.response.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member member;

    private long viewCount = 0;

    private String imgUrl;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<PostLike> postLikeList = new ArrayList<>();


    public void updateTitle(String title){
        this.title = title;
    }
    public void updateContent(String content){
        this.content = content;
    }
    public void updateImageUrl(String imgUrl){
        this.imgUrl = imgUrl;
    }

    public void increaseViewCount(){
        this.viewCount++;
    }

    public Post(PostRequestDto postRequestDto, Member member){
        this.title = postRequestDto.getTitle();
        this.content = postRequestDto.getContent();
        this.member = member;
    }
}
