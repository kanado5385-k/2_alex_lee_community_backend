package org.community.backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.domain.member.Member;
import org.community.backend.dto.request.member.MemberInfChangeRequestDTO;
import org.community.backend.dto.request.member.MemberPasswordChangeRequestDTO;
import org.community.backend.dto.request.member.SignInRequestDTO;
import org.community.backend.dto.request.member.SignUpRequestDTO;
import org.community.backend.repository.JdbcMemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // 테스트 후 DB 롤백 처리
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcMemberRepository jdbcMemberRepository;

    private int memberId;
    private final String email = "test@example.com";
    private final String newEmail = "newTest@example.com";
    private final String password = "123456";
    private final String nickname = "tester";
    private final String profileImage = "image.jpm";

    @BeforeEach
    void setUp() {
        jdbcMemberRepository.save(new Member(email, password, nickname));
        Optional<Integer> optionalMemberId = jdbcMemberRepository.findIdByEmail(email);
        memberId = optionalMemberId.get();
    }

    @Test
    @DisplayName("로그인 성공")
    void signInSuccessTest() throws Exception {
        SignInRequestDTO request = new SignInRequestDTO(email, password);

        mockMvc.perform(post("/auth") // MockMvc를 사용해서 가짜 HTTP 요청을 보냄.
                        .contentType(MediaType.APPLICATION_JSON) // JSON 데이터를 보내는 요청임을 서버에 알림
                        .content(objectMapper.writeValueAsString(request))) // 실제 HTTP 요청의 본문(body)을 설정하는 메서드
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS)) // JSON에서 code 키를 가리키는 JSONPath 표현식
                .andExpect(jsonPath("$.user_id").isNumber());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void signInWrongPasswordTest() throws Exception {
        String wrongPassword = "wrong";

        SignInRequestDTO request = new SignInRequestDTO(email, wrongPassword);

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.SIGN_IN_FAIL));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void signInEmailNotFoundTest() throws Exception {
        String wrongEmail = "wrongTest@example.com";

        SignInRequestDTO request = new SignInRequestDTO(wrongEmail, password);

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.SIGN_IN_FAIL));
    }

    @Test
    @DisplayName("회원가입 성공 - 이미지 포함")
    void registerMemberSuccess_whenImage() throws Exception {
        performSignUp_whenSuccess(newEmail, password, nickname, profileImage);
    }

    @Test
    @DisplayName("회원가입 성공 - 이미지 미포함")
    void registerMemberSuccess_whenNoImage() throws Exception {
        performSignUp_whenSuccess(newEmail, password, nickname, null);
    }

    private void performSignUp_whenSuccess(String email, String password, String nickname, String profileImage) throws Exception {
        SignUpRequestDTO request = new SignUpRequestDTO(email, password, nickname, profileImage);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복이메일")
    void registerMemberDuplicateEmail() throws Exception {
        SignUpRequestDTO request =  new SignUpRequestDTO(email, password, nickname, profileImage);

        mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.DUPLICATE_EMAIL));
    }

    @Test
    @DisplayName("사용자 정보 반환 성공")
    void getMemberSuccess() throws Exception {

        mockMvc.perform(get("/users/{userId}", memberId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.nickname").value(nickname));
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 - 이미지 포함")
    void changeMemberInfSuccess_whenImage() throws Exception {
        changeMemberInfSuccess(memberId, nickname, profileImage);
    }

    @Test
    @DisplayName("사용자 정보 수정 성공 - 이미지 미포함")
    void changeMemberInfSuccess_whenNoImage() throws Exception {
        changeMemberInfSuccess(memberId, nickname, null);
    }


    private void changeMemberInfSuccess(int memberId, String nickname, String profileImage) throws Exception {

        MemberInfChangeRequestDTO request = new MemberInfChangeRequestDTO(memberId, nickname, profileImage);

        mockMvc.perform(patch("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS));
    }

    @Test
    @DisplayName("사용자 비밀번호 수정 성공")
    void changeMemebrPasswordSuccess() throws Exception {
        MemberPasswordChangeRequestDTO request = new MemberPasswordChangeRequestDTO(memberId, password);

        mockMvc.perform(patch("/users/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(status().isOk());
    }


}