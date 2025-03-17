package org.community.backend.service;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.dto.request.member.SignInRequestDTO;
import org.community.backend.dto.request.member.SignUpRequestDto;
import org.community.backend.dto.response.member.MemberInfResponseDTO;
import org.community.backend.dto.response.member.SignInResponseDTO;
import org.community.backend.dto.response.member.SignUpResponseDto;
import org.community.backend.repository.JdbcMemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.community.backend.member.Member;

import java.util.Optional;

@Service
public class MemberService {

    private final JdbcMemberRepository jdbcMemberRepository;

    public MemberService(JdbcMemberRepository jdbcMemberRepository) {
        this.jdbcMemberRepository = jdbcMemberRepository;
    }

    @Transactional
    public ResponseEntity<? super SignUpResponseDto> registerMember(SignUpRequestDto request) {
        try {
            // 이메일 중복 체크
            if (jdbcMemberRepository.findByEmail(request.getEmail()).isPresent()) {
                return SignUpResponseDto.duplicateEmail();
            }

            // 사용자 등록
            int memberId = jdbcMemberRepository.save(new Member(request.getEmail(), request.getPassword(), request.getNickname()));

            // 프로필 이미지 등록 (이미지가 존재하는 경우)
            if (request.getProfile_image() != null && !request.getProfile_image().isEmpty()) {
                jdbcMemberRepository.saveProfileImage(memberId, request.getProfile_image());
            }

            return SignUpResponseDto.success();
        } catch (Exception e) {
            return SignUpResponseDto.databaseError();
        }
    }

    public ResponseEntity<? super SignInResponseDTO> signInMember(SignInRequestDTO request) {
        try {
            Optional<Integer> optionalMemberId = jdbcMemberRepository.findIdByEmail(request.getEmail());

            if (optionalMemberId.isEmpty()) {
                return SignInResponseDTO.mismatchLoginInf();
            }
            int memberId = optionalMemberId.get();

            String memberPassword = jdbcMemberRepository.findPasswordById(memberId).orElse(null);
            if (memberPassword == null) {
                return SignInResponseDTO.databaseError();
            }
            if (!memberPassword.equals(request.getPassword())) {
                return SignInResponseDTO.mismatchLoginInf();
            }

            return SignInResponseDTO.success(memberId);
        } catch (Exception e) {
            return SignInResponseDTO.databaseError();
        }
    }

    public ResponseEntity<? super MemberInfResponseDTO> getMemberInf(int memberId) {
        try {
            Member member = jdbcMemberRepository.findUserById(memberId).orElse(null);
            if (member == null) {
                return MemberInfResponseDTO.databaseError();
            }
            String nickname = member.getNickname();
            String profileImage = member.getImageUrl();
            return MemberInfResponseDTO.success(nickname, profileImage);
        } catch (Exception e) {
            return MemberInfResponseDTO.databaseError();
        }
    }
}