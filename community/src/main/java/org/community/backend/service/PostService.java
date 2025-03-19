package org.community.backend.service;

import jakarta.transaction.Transactional;
import org.community.backend.domain.post.Post;
import org.community.backend.domain.post.PostComment;
import org.community.backend.domain.post.PostImage;
import org.community.backend.dto.request.post.PostCommentCreateUpdateRequestDTO;
import org.community.backend.dto.request.post.PostCreateUpdateRequestDTO;
import org.community.backend.dto.response.post.PostCommentCreateResponseDTO;
import org.community.backend.dto.response.post.PostCreateResponseDTO;
import org.community.backend.dto.response.post.PostResponseDTO;
import org.community.backend.repository.JdbcMemberRepository;
import org.community.backend.repository.JpaPostCommentRepository;
import org.community.backend.repository.JpaPostImageRepository;
import org.community.backend.repository.JpaPostRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostService {
    private final JpaPostRepository jpaPostRepository;
    private final JpaPostImageRepository jpaPostImageRepository;
    private final JdbcMemberRepository jdbcMemberRepository;
    private final JpaPostCommentRepository jpaPostCommentRepository;

    public PostService(JpaPostRepository jpaPostRepository, JpaPostImageRepository jpaPostImageRepository,  JdbcMemberRepository jdbcMemberRepository,  JpaPostCommentRepository jpaPostCommentRepository) {
        this.jpaPostRepository = jpaPostRepository;
        this.jpaPostImageRepository = jpaPostImageRepository;
        this.jdbcMemberRepository = jdbcMemberRepository;
        this.jpaPostCommentRepository = jpaPostCommentRepository;
    }

    @Transactional
    public ResponseEntity<? super PostCreateResponseDTO> createPost(PostCreateUpdateRequestDTO postCreateRequestDTO) {
        try {
            Post newPost = new Post(postCreateRequestDTO.getUser_id(), postCreateRequestDTO.getPost_title(), postCreateRequestDTO.getPost_content());
            jpaPostRepository.save(newPost);

            if (postCreateRequestDTO.getPost_image() != null && !postCreateRequestDTO.getPost_image().isEmpty()) {
                PostImage postImage = new PostImage(newPost, postCreateRequestDTO.getPost_image());
                jpaPostImageRepository.save(postImage);
            }

            return PostCreateResponseDTO.success();
        } catch (Exception e) {
            return PostCreateResponseDTO.databaseError();
        }
    }

    @Transactional
    public ResponseEntity<? super PostResponseDTO> getPostById(long postId) {
        try {
            Optional<Post> post = jpaPostRepository.findById(postId);
            if (post.isPresent()) {
                Post postEntity = post.get();
                Optional<String> writer = jdbcMemberRepository.findEmailById(postEntity.getMemberId());
                if (writer.isPresent()) {
                    return PostResponseDTO.success(postEntity, writer.get());
                }
            }
            return PostResponseDTO.postNotFound();
        } catch (Exception e) {
            return PostResponseDTO.databaseError();
        }
    }

    @Transactional
    public ResponseEntity<? super PostCommentCreateResponseDTO> createPostComment(PostCommentCreateUpdateRequestDTO postCommentCreateRequestDTO, Long postId) {
        try {
            Optional<Post> post = jpaPostRepository.findById(postId);
            if (post.isPresent()) {
                Post postEntity = post.get();
                PostComment postComment = new PostComment(postCommentCreateRequestDTO.getUser_id(), postEntity, postCommentCreateRequestDTO.getComment_content());
                jpaPostCommentRepository.save(postComment);
                return PostCommentCreateResponseDTO.success();
            }
            return PostCommentCreateResponseDTO.postNotFound();
        }
        catch (Exception e) {
            return PostCommentCreateResponseDTO.databaseError();
        }
    }
}
