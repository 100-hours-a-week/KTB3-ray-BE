package com.example.spring_practice.domain.member.service;

import com.example.spring_practice.domain.member.dto.*;
import com.example.spring_practice.domain.member.entity.Member;
import com.example.spring_practice.domain.member.repository.MemberRepository;
import com.example.spring_practice.domain.shared.ImageService;
import com.example.spring_practice.global.response.CustomException;
import com.example.spring_practice.global.response.ErrorCode;
import com.example.spring_practice.global.security.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public JwtTokenResponseDto login(LoginRequestDto loginRequestDto) {
        try {
            Authentication authRequest =
                    new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());
            Authentication authResult = authenticationManager.authenticate(authRequest);
            String token = jwtUtil.generateToken(authResult.getName());
            return AuthDtoConverter.toJwtTokenResponseDto(token);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.LOGIN_FAILED);
        }

    }

    public Member getCurrentMember() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        if (email == null) {
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
    }

}
