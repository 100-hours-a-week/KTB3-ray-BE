package com.example.spring_practice.domain.post.repository;

import com.example.spring_practice.domain.post.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike,Long> {
    boolean existsByPost_PostIdAndMember_MemberId(Long postId, Long memberId);

    void deleteByPost_PostIdAndMember_MemberId(Long postId, Long memberId);

    Optional<PostLike> findByPost_PostIdAndMember_MemberId(Long postId, Long memberId);
}
