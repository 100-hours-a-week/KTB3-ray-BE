package com.example.spring_practice.domain.member.dto;

import org.springframework.stereotype.Component;

@Component
public class AuthDtoConverter {
    static public JwtTokenResponseDto toJwtTokenResponseDto(String accessToken){
        return new JwtTokenResponseDto(accessToken);
    }
}
