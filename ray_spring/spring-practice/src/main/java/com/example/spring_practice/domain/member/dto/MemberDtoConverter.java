package com.example.spring_practice.domain.member.dto;

import com.example.spring_practice.domain.member.entity.Member;
import com.example.spring_practice.domain.shared.ImageService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class MemberDtoConverter {
    static public ProfileResponseDto toProfileResponseDto(Member member, String url){
        return new ProfileResponseDto(member.getEmail(), member.getNickname(), url);
    }

    static public DuplicateCheckResponseDto toDuplicateCheckResponseDto(boolean isDuplicate){
        return new DuplicateCheckResponseDto(isDuplicate);
    }
}
