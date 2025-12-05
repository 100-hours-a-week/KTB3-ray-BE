package com.example.spring_practice.domain.member.repository;

import com.example.spring_practice.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    public Optional<Member> findByEmail(String email);
    public boolean existsByEmail(String email);
    public boolean existsByNickname(String nickname);

    public Optional<Member> findByNickname(String nickname);
}
