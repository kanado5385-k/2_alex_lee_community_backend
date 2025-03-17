package org.community.backend.service;

import org.community.backend.repository.JdbcMemberRepository;
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
    public void registerUser(String email, String password, String nickname, String profileImage) {
        // 이메일 중복 체크
        if (jdbcMemberRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already taken.");
        }

        int memberId = jdbcMemberRepository.save(new Member(email, password, nickname));

        // 프로필 이미지 등록 (이미지가 등록됐을 경우)
        if (profileImage != null && !profileImage.isEmpty()) {
            jdbcMemberRepository.saveProfileImage(memberId, profileImage);
        }
    }
}
