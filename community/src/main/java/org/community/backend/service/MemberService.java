package org.community.backend.service;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.dto.response.member.SignUpResponseDto;
import org.community.backend.repository.JdbcMemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.community.backend.member.Member;

@Service
public class MemberService {

    private final JdbcMemberRepository jdbcMemberRepository;

    public MemberService(JdbcMemberRepository jdbcMemberRepository) {
        this.jdbcMemberRepository = jdbcMemberRepository;
    }

    @Transactional
    public ResponseEntity<? super SignUpResponseDto> registerUser(String email, String password, String nickname, String profileImage) {
        // <? super SignUpResponseDto> ->  SignUpResponseDto 또는 그 부모 타입(ApiResponse)을 반환할 수 있도록

        // 이메일 중복 체크
        if (jdbcMemberRepository.findByEmail(email).isPresent()) {
            return SignUpResponseDto.duplicateEmail();
        }

        // 사용자 등록
        int memberId = jdbcMemberRepository.save(new Member(email, password, nickname));

        // 프로필 이미지 등록 (이미지가 존재하는 경우)
        if (profileImage != null && !profileImage.isEmpty()) {
            jdbcMemberRepository.saveProfileImage(memberId, profileImage);
        }

        return SignUpResponseDto.success();
    }
}
