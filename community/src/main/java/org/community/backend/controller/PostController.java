package org.community.backend.controller;

import jakarta.validation.Valid;
import org.community.backend.dto.request.post.PostCommentCreateUpdateRequestDTO;
import org.community.backend.dto.request.post.PostCommentDeleteRequestDTO;
import org.community.backend.dto.request.post.PostCreateUpdateRequestDTO;
import org.community.backend.dto.request.post.PostLikeRequestDTO;
import org.community.backend.dto.response.post.*;
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
    public ResponseEntity<? super PostCreateUpdateResponseDTO> createPost(@Valid @RequestBody PostCreateUpdateRequestDTO request) {
        return postService.createPost(request);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<? super PostResponseDTO> getPost(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<? super PostCommentCreateUpdateResponseDTO> createComment(@PathVariable Long postId, @Valid @RequestBody PostCommentCreateUpdateRequestDTO request) {
        return postService.createPostComment(request,postId);
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<? super PostCreateUpdateResponseDTO> updatePost(@PathVariable Long postId, @Valid @RequestBody PostCreateUpdateRequestDTO request) {
        return postService.updatePost(request,postId);
    }

    @PatchMapping("/comments/{commentId}")
    public ResponseEntity<? super PostCommentCreateUpdateResponseDTO> updateComment(@PathVariable Long commentId, @Valid @RequestBody PostCommentCreateUpdateRequestDTO request) {
        return postService.updateComment(request,commentId);
    }

    @PutMapping("/likes/{postId}")
    public ResponseEntity<? super PostLikeResponseDTO> togglePostLike(@PathVariable Long postId, @RequestBody PostLikeRequestDTO request) {
        return postService.togglePostLike(request, postId);
    }

    @GetMapping
    public ResponseEntity<? super PostListResponseDTO> getAllPosts() {
        return postService.getAllPosts();
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<? super PostCommentListResponseDTO> getCommentsByPostId(@PathVariable Long postId) {
        return postService.getAllCommentsByPostId(postId);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<? super PostCommentDeleteResponseDTO> deleteComment(@PathVariable Long commentId, @RequestBody PostCommentDeleteRequestDTO request) {
        return postService.deleteComment(request, commentId);
    }
}

