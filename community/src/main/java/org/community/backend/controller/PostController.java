package org.community.backend.controller;

import jakarta.validation.Valid;
import org.community.backend.dto.request.post.PostCreateRequestDTO;
import org.community.backend.dto.response.post.PostCreateResponseDTO;
import org.community.backend.dto.response.post.PostResponseDTO;
import org.community.backend.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("{postsId}")
    public ResponseEntity<? super PostResponseDTO> registerPost(@PathVariable Long postsId) {
        return postService.getPostById(postsId);
    }
}

