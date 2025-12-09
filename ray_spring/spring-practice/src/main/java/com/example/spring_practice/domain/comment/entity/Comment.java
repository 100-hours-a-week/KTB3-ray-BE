package com.example.spring_practice.domain.comment.entity;

import com.example.spring_practice.domain.comment.dto.CommentRequestDto;
import com.example.spring_practice.domain.member.entity.Member;
import com.example.spring_practice.domain.post.entity.Post;
import com.example.spring_practice.global.response.CustomException;
import com.example.spring_practice.global.response.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    private String content;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean deleted = false;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    public Comment(CommentRequestDto commentRequestDto, Member member, Post post){
        this.content = commentRequestDto.getContent();
        this.member = member;
        this.post = post;
    }
    public void updateContent(String content){
        this.content = content;
    }
}
