package org.community.backend.service;

import jakarta.transaction.Transactional;
import org.community.backend.domain.entity.Post;
import org.community.backend.domain.entity.PostComment;
import org.community.backend.domain.entity.PostImage;
import org.community.backend.domain.entity.PostLike;
import org.community.backend.dto.request.post.PostCommentCreateUpdateRequestDTO;
import org.community.backend.dto.request.post.PostCommentDeleteRequestDTO;
import org.community.backend.dto.request.post.PostCreateUpdateRequestDTO;
import org.community.backend.dto.request.post.PostLikeRequestDTO;
import org.community.backend.dto.response.post.*;
import org.community.backend.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {
    private final JpaPostRepository jpaPostRepository;
    private final JpaPostImageRepository jpaPostImageRepository;
    private final JdbcMemberRepository jdbcMemberRepository;
    private final JpaPostCommentRepository jpaPostCommentRepository;
    private final JpaPostLikeRepository jpaPostLikeRepository;

    public PostService(JpaPostRepository jpaPostRepository, JpaPostImageRepository jpaPostImageRepository,  JdbcMemberRepository jdbcMemberRepository,  JpaPostCommentRepository jpaPostCommentRepository, JpaPostLikeRepository jpaPostLikeRepository) {
        this.jpaPostRepository = jpaPostRepository;
        this.jpaPostImageRepository = jpaPostImageRepository;
        this.jdbcMemberRepository = jdbcMemberRepository;
        this.jpaPostCommentRepository = jpaPostCommentRepository;
        this.jpaPostLikeRepository = jpaPostLikeRepository;
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
                    postEntity.incrementViews();
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
                postEntity.incrementCommentCount();
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

    public ResponseEntity<? super PostCommentCreateUpdateResponseDTO> updateComment(PostCommentCreateUpdateRequestDTO postCommentCreateRequestDTO, Long commentId) {
        try {
            Optional<PostComment> postComment = jpaPostCommentRepository.findById(commentId);
            if (postComment.isPresent()) {
                PostComment postCommentEntity = postComment.get();
                Long memberId = postCommentEntity.getMemberId();
                if (!postCommentCreateRequestDTO.getUser_id().equals(memberId)) {
                    return PostCommentCreateUpdateResponseDTO.notHavePermission();
                }

                postCommentEntity.updateComment(postCommentCreateRequestDTO);
                jpaPostCommentRepository.save(postCommentEntity);

                return PostCommentCreateUpdateResponseDTO.success();
            }
            return PostCommentCreateUpdateResponseDTO.commentNotFound();
        } catch (Exception e) {
            return PostCommentCreateUpdateResponseDTO.databaseError();
        }
    }

    @Transactional
    public ResponseEntity<? super PostLikeResponseDTO> togglePostLike(PostLikeRequestDTO postLikeRequestDTO, Long postId) {
        try {
            Optional<Post> post = jpaPostRepository.findById(postId);
            if (post.isPresent()) {
                Post postEntity = post.get();
                Optional<PostLike> existingLike = jpaPostLikeRepository.findByMemberIdAndPost(postLikeRequestDTO.getUser_id(), postEntity);
                if (existingLike.isPresent()) {
                    jpaPostLikeRepository.delete(existingLike.get());
                    postEntity.decrementLikeCount();
                    return PostLikeResponseDTO.success();
                }
                PostLike postLike = new PostLike(postLikeRequestDTO.getUser_id(), postEntity);
                jpaPostLikeRepository.save(postLike);
                postEntity.incrementLikeCount();
                return PostLikeResponseDTO.success();

            }
            return PostLikeResponseDTO.postNotFound();
        } catch (Exception e) {
            return PostLikeResponseDTO.databaseError();
        }
    }

    public ResponseEntity<? super PostListResponseDTO> getAllPosts() {
        try {
            List<Post> posts = jpaPostRepository.findAll();
            if (posts.isEmpty()) {
                return PostListResponseDTO.noAnyPostFound();
            }

            List<String> writers = posts.stream()
                    .map(post -> jdbcMemberRepository.findEmailById(post.getMemberId()).orElse("Unknown"))
                    .collect(Collectors.toList());

            return PostListResponseDTO.success(posts, writers);
        } catch (Exception e) {
            return PostListResponseDTO.databaseError();
        }

    }

    @Transactional
    public ResponseEntity<? super PostCommentListResponseDTO> getAllCommentsByPostId(Long postId) {
        try {
            Optional<Post> postOptional = jpaPostRepository.findById(postId);
            if (postOptional.isEmpty()) {
                return PostCommentListResponseDTO.postNotFound();
            }

            List<PostComment> comments = jpaPostCommentRepository.findByPostIdOrderByCreatedAtDesc(postId);
            if (comments.isEmpty()) {
                return PostCommentListResponseDTO.noAnyCommentFound();
            }

            List<String> writers = comments.stream()
                    .map(comment -> jdbcMemberRepository.findEmailById(comment.getMemberId()).orElse("Unknown"))
                    .collect(Collectors.toList());

            return PostCommentListResponseDTO.success(comments, writers);
        } catch (Exception e) {
            return PostCommentListResponseDTO.databaseError();
        }
    }

    @Transactional
    public ResponseEntity<? super PostCommentDeleteResponseDTO> deleteComment(PostCommentDeleteRequestDTO postCommentDeleteRequestDTO, Long commentId) {
        try {
            PostComment postComment = jpaPostCommentRepository.getReferenceById(commentId);
            Long memberId = postComment.getMemberId();
            if (!postCommentDeleteRequestDTO.getUser_id().equals(memberId)) {
                return PostCommentDeleteResponseDTO.notHavePermission();
            }

            postComment.getPost().decrementCommentCount();

            jpaPostCommentRepository.delete(postComment);
            return PostCommentDeleteResponseDTO.success();
        }
        catch (Exception e) {
            return PostCommentDeleteResponseDTO.databaseError();
        }
    }


}
