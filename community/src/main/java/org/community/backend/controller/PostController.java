package org.community.backend.controller;

import jakarta.validation.Valid;
import org.community.backend.domain.entity.Post;
import org.community.backend.dto.request.member.SignUpRequestDto;
import org.community.backend.dto.request.post.PostCreateRequestDTO;
import org.community.backend.dto.response.member.SignUpResponseDto;
import org.community.backend.dto.response.post.PostCreateResponseDTO;
import org.community.backend.service.MemberService;
import org.community.backend.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<? super PostCreateResponseDTO> registerMember(@Valid @RequestBody PostCreateRequestDTO request) {
        return postService.createPost(request);
    }
}

