package org.community.backend.service;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.domain.member.Member;
import org.community.backend.dto.request.member.SignUpRequestDto;
import org.community.backend.dto.response.member.SignUpResponseDto;
import org.community.backend.repository.JdbcMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private JdbcMemberRepository jdbcMemberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입시 중복 이메일 발생")
    void registerMember_shouldReturnDuplicateEmail_whenEmailExists() {
        // given
        SignUpRequestDto request = new SignUpRequestDto("test@email.com", "password123", "nickname", null);
        when(jdbcMemberRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new Member("tes@email.com", "password123", "nickname")));
        // DB를 조회하지 않고, 직접 만든 Member 객체가 리턴되게 Mock하는 코드

        // when
        ResponseEntity<?> response = memberService.registerMember(request);
        ApiResponse body = (ApiResponse) response.getBody();

        // then
        assertEquals(ResponseCode.DUPLICATE_EMAIL, body.getCode());
        verify(jdbcMemberRepository, never()).save(any());
        // 실제 저장이 일어나지 않았는지 확인
    }

    @Test
    @DisplayName("회원가입 성공 - 이미지 미포함")
    void registerMember_shouldRegisterSuccessfully_withoutProfileImage() {
        // given
        SignUpRequestDto request = new SignUpRequestDto("test@email.com", "password123", "nickname", null);
        when(jdbcMemberRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(jdbcMemberRepository.save(any(Member.class))).thenReturn(1);

        // when
        ResponseEntity<?> response = memberService.registerMember(request);
        ApiResponse body = (ApiResponse) response.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, body.getCode());
        verify(jdbcMemberRepository, times(1)).save(any(Member.class));
        verify(jdbcMemberRepository, never()).saveProfileImage(anyInt(), anyString());
    }

    @Test
    @DisplayName("회원가입 성공 - 이미지 포함")
    void registerMember_shouldRegisterSuccessfully_withProfileImage() {
        // given
        SignUpRequestDto request = new SignUpRequestDto("test@email.com", "password123", "nickname", "profile.jpg");
        when(jdbcMemberRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(jdbcMemberRepository.save(any(Member.class))).thenReturn(1);

        // when
        ResponseEntity<?> response = memberService.registerMember(request);
        ApiResponse body = (ApiResponse) response.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, body.getCode());
        verify(jdbcMemberRepository, times(1)).saveProfileImage(eq(1), eq("profile.jpg"));
    }

    @Test
    @DisplayName("회원가입 성공 - 이미지 포함")
    void registerMember_shouldReturnDatabaseError_whenExceptionOccurs() {
        // given
        SignUpRequestDto request = new SignUpRequestDto("test@email.com", "password123", "nickname", null);
        when(jdbcMemberRepository.findByEmail(anyString())).thenThrow(new RuntimeException());

        // when
        ResponseEntity<?> response = memberService.registerMember(request);
        ApiResponse body = (ApiResponse) response.getBody();

        // then
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, body.getCode());
    }
}
