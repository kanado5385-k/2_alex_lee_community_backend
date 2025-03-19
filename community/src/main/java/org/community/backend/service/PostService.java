package org.community.backend.service;

import jakarta.transaction.Transactional;
import org.community.backend.domain.post.Post;
import org.community.backend.domain.post.PostComment;
import org.community.backend.domain.post.PostImage;
import org.community.backend.dto.request.post.PostCommentCreateUpdateRequestDTO;
import org.community.backend.dto.request.post.PostCreateUpdateRequestDTO;
import org.community.backend.dto.response.post.PostCommentCreateUpdateResponseDTO;
import org.community.backend.dto.response.post.PostCreateUpdateResponseDTO;
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
    public ResponseEntity<? super PostCreateUpdateResponseDTO> createPost(PostCreateUpdateRequestDTO postCreateRequestDTO) {
        try {
            Post newPost = new Post(postCreateRequestDTO.getUser_id(), postCreateRequestDTO.getPost_title(), postCreateRequestDTO.getPost_content());
            jpaPostRepository.save(newPost);

            if (postCreateRequestDTO.getPost_image() != null && !postCreateRequestDTO.getPost_image().isEmpty()) {
                PostImage postImage = new PostImage(newPost, postCreateRequestDTO.getPost_image());
                jpaPostImageRepository.save(postImage);
            }

            return PostCreateUpdateResponseDTO.success();
        } catch (Exception e) {
            return PostCreateUpdateResponseDTO.databaseError();
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
    public ResponseEntity<? super PostCommentCreateUpdateResponseDTO> createPostComment(PostCommentCreateUpdateRequestDTO postCommentCreateRequestDTO, Long postId) {
        try {
            Optional<Post> post = jpaPostRepository.findById(postId);
            if (post.isPresent()) {
                Post postEntity = post.get();
                PostComment postComment = new PostComment(postCommentCreateRequestDTO.getUser_id(), postEntity, postCommentCreateRequestDTO.getComment_content());
                jpaPostCommentRepository.save(postComment);
                return PostCommentCreateUpdateResponseDTO.success();
            }
            return PostCommentCreateUpdateResponseDTO.postNotFound();
        }
        catch (Exception e) {
            return PostCommentCreateUpdateResponseDTO.databaseError();
        }
    }

    @Transactional
    public ResponseEntity<? super PostCreateUpdateResponseDTO> updatePost(PostCreateUpdateRequestDTO postCreateUpdateRequestDTO, Long postId) {
        try {
            Optional<Post> post = jpaPostRepository.findById(postId);
            if (post.isPresent()) {
                Post postEntity = post.get();
                Long memberId = postEntity.getMemberId();
                if (!postCreateUpdateRequestDTO.getUser_id().equals(memberId)) {
                    return PostCreateUpdateResponseDTO.notHavePermission();
                }
                postEntity.updatePost(postCreateUpdateRequestDTO);

                if (postCreateUpdateRequestDTO.getPost_image() != null && !postCreateUpdateRequestDTO.getPost_image().isEmpty()) {
                    Optional<PostImage> postImage = jpaPostImageRepository.findByPostId(postId);
                    if (postImage.isPresent()) {
                        PostImage postImageEntity = postImage.get();
                        postImageEntity.updateImageUrl(postCreateUpdateRequestDTO.getPost_image());
                        return PostCreateUpdateResponseDTO.success();
                    }
                    PostImage newPostImage = new PostImage(postEntity, postCreateUpdateRequestDTO.getPost_image());
                    jpaPostImageRepository.save(newPostImage);
                    return PostCreateUpdateResponseDTO.success();
                }

                return PostCreateUpdateResponseDTO.success();
            }
            return PostCreateUpdateResponseDTO.postNotFound();
        } catch (Exception e) {
            return PostCreateUpdateResponseDTO.databaseError();
        }
    }
}
