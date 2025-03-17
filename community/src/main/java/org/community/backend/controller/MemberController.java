package org.community.backend.controller;

import jakarta.validation.Valid;
import org.community.backend.dto.request.member.SignUpRequestDto;
import org.community.backend.repository.JdbcMemberRepository;
import org.community.backend.service.MemberService;

import org.community.backend.dto.response.member.SignUpResponseDto;
import org.community.backend.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping
    public ResponseEntity<? super SignUpResponseDto> registerUser(@Valid @RequestBody SignUpRequestDto request) {
        return memberService.registerUser(request);
    }
}