package org.community.backend.service;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.domain.member.Member;
import org.community.backend.dto.request.member.MemberInfChangeRequestDTO;
import org.community.backend.dto.request.member.SignInRequestDTO;
import org.community.backend.dto.request.member.SignUpRequestDTO;
import org.community.backend.dto.response.member.MemberInfChangeResponseDTO;
import org.community.backend.dto.response.member.MemberInfResponseDTO;
import org.community.backend.dto.response.member.SignInResponseDTO;
import org.community.backend.repository.JdbcMemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    private final int userId = 1;
    private final String rawEmail = "test@email.com";
    private final String rawPassword = "password123";
    private final String rawNickname = "nickname";
    private final String rawImage = "profile.jpg";
    private final String savedPassword = "password123";

    @Mock
    private JdbcMemberRepository jdbcMemberRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void registerMember_shouldReturnDuplicateEmail_whenEmailExists() {
        // given
        SignUpRequestDTO request = new SignUpRequestDTO(rawEmail, rawPassword, rawNickname, null);
        when(jdbcMemberRepository.findByEmail(request.getEmail()))
                .thenReturn(Optional.of(new Member(rawEmail, rawPassword, rawNickname)));
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
        SignUpRequestDTO request = new SignUpRequestDTO(rawEmail, rawPassword, rawNickname, null);
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
        SignUpRequestDTO request = new SignUpRequestDTO(rawEmail, rawPassword, rawNickname, rawImage);
        when(jdbcMemberRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(jdbcMemberRepository.save(any(Member.class))).thenReturn(userId);

        // when
        ResponseEntity<?> response = memberService.registerMember(request);
        ApiResponse body = (ApiResponse) response.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, body.getCode());
        verify(jdbcMemberRepository, times(1)).saveProfileImage(eq(userId), eq(rawImage));
    }

    @Test
    @DisplayName("회원가입 실패 - 서버 오류")
    void registerMember_shouldReturnDatabaseError_whenExceptionOccurs() {
        // given
        SignUpRequestDTO request = new SignUpRequestDTO(rawEmail, rawPassword, rawNickname, null);
        when(jdbcMemberRepository.findByEmail(anyString())).thenThrow(new RuntimeException());

        // when
        ResponseEntity<?> response = memberService.registerMember(request);
        ApiResponse body = (ApiResponse) response.getBody();

        // then
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, body.getCode());
    }

    @Test
    @DisplayName("로그인 성공")
    void signIn_shouldReturnSuccess_whenUserIsSignedIn() {
        // given
        SignInRequestDTO request = new SignInRequestDTO(rawEmail, rawPassword);
        when(jdbcMemberRepository.findIdByEmail(request.getEmail())).thenReturn(Optional.of(userId));
        when(jdbcMemberRepository.findPasswordById(userId)).thenReturn(Optional.of(savedPassword));

        // when
        ResponseEntity<?> response = memberService.signInMember(request);
        SignInResponseDTO body = (SignInResponseDTO) response.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, body.getCode());
        assertEquals(userId, body.getUser_id());
        verify(jdbcMemberRepository, times(1)).findIdByEmail(request.getEmail());
        verify(jdbcMemberRepository, times(1)).findPasswordById(userId);
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 틀림")
    void signIn_shouldReturnMismatchLoginInf_whenWrongEmail() {

        // given
        SignInRequestDTO request = new SignInRequestDTO(rawEmail, rawPassword);
        when(jdbcMemberRepository.findIdByEmail(request.getEmail())).thenReturn(Optional.empty());

        // when
        ResponseEntity<?> response = memberService.signInMember(request);
        ApiResponse body = (ApiResponse) response.getBody();

        // then
        assertEquals(ResponseCode.SIGN_IN_FAIL, body.getCode());
        verify(jdbcMemberRepository, times(1)).findIdByEmail(request.getEmail());
        verify(jdbcMemberRepository, never()).findPasswordById(anyInt());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 틀림")
    void signIn_shouldReturnMismatchLoginInf_whenWrongPassword() {
        String wrongPassword = "wrongPassword";

        // given
        SignInRequestDTO request = new SignInRequestDTO(rawEmail, wrongPassword);
        when(jdbcMemberRepository.findIdByEmail(request.getEmail())).thenReturn(Optional.of(userId));
        when(jdbcMemberRepository.findPasswordById(userId)).thenReturn(Optional.of(savedPassword));

        // when
        ResponseEntity<?> response = memberService.signInMember(request);
        ApiResponse body = (ApiResponse) response.getBody();

        // then
        assertEquals(ResponseCode.SIGN_IN_FAIL, body.getCode());
        verify(jdbcMemberRepository, times(1)).findIdByEmail(request.getEmail());
        verify(jdbcMemberRepository, times(1)).findPasswordById(userId);
    }

    @Test
    @DisplayName("로그인 실패 - 서버 오류")
    void signIn_shouldReturnServerError_whenExceptionOccurs() {
        // given
        SignInRequestDTO request = new SignInRequestDTO(rawEmail, rawPassword);
        when(jdbcMemberRepository.findIdByEmail(request.getEmail())).thenReturn(Optional.of(userId));
        when(jdbcMemberRepository.findPasswordById(userId)).thenReturn(Optional.empty());

        // when
        ResponseEntity<?> response = memberService.signInMember(request);
        ApiResponse body = (ApiResponse) response.getBody();

        // then
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, body.getCode());
        verify(jdbcMemberRepository, times(1)).findIdByEmail(request.getEmail());
        verify(jdbcMemberRepository, times(1)).findPasswordById(userId);
    }

    @Test
    @DisplayName("사용자 정보 반환 성공")
    void getMemberInf_shouldReturnSuccess() {
        //given
        when(jdbcMemberRepository.findUserById(userId)).thenReturn(Optional.of(new Member(rawNickname, rawImage)));

        // when
        ResponseEntity<?> response = memberService.getMemberInf(userId);
        MemberInfResponseDTO body = (MemberInfResponseDTO) response.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, body.getCode());
        assertEquals(rawNickname, body.getNickname());
        assertEquals(rawImage, body.getProfileImage());
        verify(jdbcMemberRepository, times(1)).findUserById(userId);
    }

    @Test
    @DisplayName("사용자 정보 반환 실패 - 서버 오류")
    void getMemberInf_shouldReturnServerError_whenExceptionOccurs() {
        //given
        when(jdbcMemberRepository.findUserById(userId)).thenReturn(Optional.empty());

        // when
        ResponseEntity<?> response = memberService.getMemberInf(userId);
        ApiResponse body = (ApiResponse) response.getBody();

        // then
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, body.getCode());
        verify(jdbcMemberRepository, times(1)).findUserById(userId);
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 - 이미지 포함")
    void changeMemberInf_shouldReturnSuccess_withImage() {
        // given
        MemberInfChangeRequestDTO memberInfChangeRequestDTO = new MemberInfChangeRequestDTO(userId, rawNickname, rawImage);

        // when
        ResponseEntity<?> response = memberService.changeMemberInf(memberInfChangeRequestDTO);
        MemberInfChangeResponseDTO body = (MemberInfChangeResponseDTO) response.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, body.getCode());
        verify(jdbcMemberRepository, times(1)).updateMemberInfoWithImage(userId, rawNickname, rawImage);
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 - 이미지 미포함")
    void changeMemberInf_shouldReturnSuccess_withoutImage() {
        // given
        MemberInfChangeRequestDTO memberInfChangeRequestDTO = new MemberInfChangeRequestDTO(userId, rawNickname, null);

        // when
        ResponseEntity<?> response = memberService.changeMemberInf(memberInfChangeRequestDTO);
        MemberInfChangeResponseDTO body = (MemberInfChangeResponseDTO) response.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, body.getCode());
        verify(jdbcMemberRepository, times(1)).updateMemberNickname(userId, rawNickname);
    }

    @Test
    @DisplayName("사용자 정보 수정 실패 - 서버 오류")
    void changeMemberInf_shouldReturnServerError_whenExceptionOccurs() {
        // given
        MemberInfChangeRequestDTO memberInfChangeRequestDTO = new MemberInfChangeRequestDTO(userId, rawNickname, rawImage);
        doThrow(new RuntimeException())
                .when(jdbcMemberRepository)
                .updateMemberInfoWithImage(userId, rawNickname, rawImage);

        // when
        ResponseEntity<?> response = memberService.changeMemberInf(memberInfChangeRequestDTO);
        ApiResponse body = (ApiResponse) response.getBody();

        // then
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, body.getCode());
        verify(jdbcMemberRepository, never()).updateMemberNickname(anyInt(), anyString());
    }
}
