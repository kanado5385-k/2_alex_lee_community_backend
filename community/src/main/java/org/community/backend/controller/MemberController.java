package org.community.backend.controller;

import jakarta.validation.Valid;
import org.community.backend.dto.request.member.MemberInfChangeRequestDTO;
import org.community.backend.dto.request.member.MemberPasswordChangeRequestDTO;
import org.community.backend.dto.request.member.SignInRequestDTO;
import org.community.backend.dto.request.member.SignUpRequestDTO;
import org.community.backend.dto.response.member.*;
import org.community.backend.service.MemberService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/users")
    public ResponseEntity<? super SignUpResponseDto> registerMember(@Valid @RequestBody SignUpRequestDTO request) {
        return memberService.registerMember(request);
    }

    @PostMapping("/auth")
    public ResponseEntity<? super SignInResponseDTO> signInMember(@Valid @RequestBody SignInRequestDTO request) {
        return memberService.signInMember(request);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<? super MemberInfResponseDTO> getMemberInf(@PathVariable int userId) {
        return memberService.getMemberInf(userId);
    }

    @PatchMapping("/users")
    public ResponseEntity<? super MemberInfChangeResponseDTO>  updateMemberInf(@Valid @RequestBody MemberInfChangeRequestDTO request) {
        return memberService.changeMemberInf(request);
    }

    @PatchMapping("/users/password")
    public ResponseEntity<? super MemberPasswordChangeResponseDTO>  updateMemberPassword(@Valid @RequestBody MemberPasswordChangeRequestDTO request) {
        return memberService.changeMemberPassword(request);
    }

    @DeleteMapping("users/{userId}")
    public ResponseEntity<? super MemberDeleteResponseDTO>  deleteMember(@PathVariable int userId) {
        return memberService.deleteMember(userId);
    }
}