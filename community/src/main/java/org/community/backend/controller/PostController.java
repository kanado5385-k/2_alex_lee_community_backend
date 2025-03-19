package org.community.backend.controller;

import jakarta.validation.Valid;
import org.community.backend.dto.request.post.PostCommentCreateUpdateRequestDTO;
import org.community.backend.dto.request.post.PostCreateUpdateRequestDTO;
import org.community.backend.dto.response.post.PostCommentCreateUpdateResponseDTO;
import org.community.backend.dto.response.post.PostCreateUpdateResponseDTO;
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
    public ResponseEntity<? super PostCreateUpdateResponseDTO> registerMember(@Valid @RequestBody PostCreateUpdateRequestDTO request) {
        return postService.createPost(request);
    }

    @GetMapping("{postsId}")
    public ResponseEntity<? super PostResponseDTO> registerPost(@PathVariable Long postsId) {
        return postService.getPostById(postsId);
    }

    @PostMapping("/{postsId}/comments")
    public ResponseEntity<? super PostCommentCreateUpdateResponseDTO> registerComment(@PathVariable Long postsId, @Valid @RequestBody PostCommentCreateUpdateRequestDTO request) {
        return postService.createPostComment(request,postsId);
    }
}

