package com.example.spring_practice.domain.member.service;

import com.example.spring_practice.domain.member.dto.*;
import com.example.spring_practice.domain.member.entity.Member;
import com.example.spring_practice.domain.member.repository.MemberRepository;
import com.example.spring_practice.domain.shared.ImageService;
import com.example.spring_practice.global.response.CustomException;
import com.example.spring_practice.global.response.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public void signUp(SignUpRequestDto signUpRequestDto) {
        if(emailDuplicateCheck(signUpRequestDto.getEmail()).isDuplicated()){
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if(nicknameDuplicateCheck(signUpRequestDto.getNickname()).isDuplicated()){
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
        Member member = new Member(signUpRequestDto.getEmail(), passwordEncoder.encode(signUpRequestDto.getPassword()), signUpRequestDto.getNickname());
        if(signUpRequestDto.getProfileImage()!=null){
            member.updateImageUrl(imageService.saveImg(signUpRequestDto.getProfileImage()));
        }
        memberRepository.save(member);
    }

    public ProfileResponseDto getMyProfile() {

        return MemberDtoConverter.toProfileResponseDto(authService.getCurrentMember(), imageService.getFullImgUrl(authService.getCurrentMember().getProfileImgUrl()));
    }

    @Transactional
    public void editPassword(EditPasswordRequestDto password) {
        authService.getCurrentMember().editPassword(passwordEncoder.encode(password.getPassword()));
    }

    @Transactional
    public void editProfile(EditProfileRequestDto editProfileRequestDto) {
        if(editProfileRequestDto.getProfileImage() != null){
            authService.getCurrentMember().updateImageUrl(imageService.saveImg(editProfileRequestDto.getProfileImage()));
        }
        if(editProfileRequestDto.getNickname()!= null){
            authService.getCurrentMember().updateNickname(editProfileRequestDto.getNickname());
        }
    }

    public DuplicateCheckResponseDto emailDuplicateCheck(String email) {
        return MemberDtoConverter.toDuplicateCheckResponseDto(memberRepository.existsByEmail(email));
    }

    public DuplicateCheckResponseDto nicknameDuplicateCheck(String nickname) {
        return MemberDtoConverter.toDuplicateCheckResponseDto(memberRepository.existsByNickname(nickname));
    }
}
