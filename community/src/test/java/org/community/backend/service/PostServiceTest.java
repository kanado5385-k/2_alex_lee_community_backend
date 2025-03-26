package org.community.backend.service;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.domain.entity.Post;
import org.community.backend.domain.entity.PostComment;
import org.community.backend.domain.entity.PostImage;
import org.community.backend.dto.request.post.PostCommentCreateUpdateRequestDTO;
import org.community.backend.dto.request.post.PostCreateUpdateRequestDTO;
import org.community.backend.dto.response.post.PostCommentCreateUpdateResponseDTO;
import org.community.backend.dto.response.post.PostCreateUpdateResponseDTO;
import org.community.backend.dto.response.post.PostResponseDTO;
import org.community.backend.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.community.backend.repository.JpaPostCommentRepository;
import org.community.backend.repository.JpaPostRepository;
import org.community.backend.repository.JpaPostImageRepository;
import org.community.backend.repository.JdbcMemberRepository;
import org.community.backend.repository.JpaPostLikeRepository;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    private final Long userId = 1L;
    private final Long wrongUserId = 3L;
    private final String userEmail = "test@email.com";

    private final Long postId = 2L;
    private final String postTitle = "test post";
    private final String postContent = "test post content";
    private final String postImageUrl = "test post image url";

    private final String commentContent = "test comment content";
    private final Long commentId = 5L;

    @Mock
    private JpaPostRepository jpaPostRepository;

    @Mock
    private JpaPostImageRepository jpaPostImageRepository;

    @Mock
    private JdbcMemberRepository jdbcMemberRepository;

    @Mock
    private JpaPostCommentRepository jpaPostCommentRepository;

    @Mock
    private JpaPostLikeRepository jpaPostLikeRepository;

    @InjectMocks
    PostService postService;

    @Test
    @DisplayName("게시글 작성 성공 - 이미지 포함")
    void createPost_shouldReturnSuccess_whenImageIsNotEmpty() {
        // given
        PostCreateUpdateRequestDTO postCreateUpdateRequestDTO = new PostCreateUpdateRequestDTO(userId, postTitle, postContent, postImageUrl);

        // when
        ResponseEntity<?> responseEntity = postService.createPost(postCreateUpdateRequestDTO);
        PostCreateUpdateResponseDTO postCreateUpdateResponseDTO = (PostCreateUpdateResponseDTO) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, postCreateUpdateResponseDTO.getCode());
        verify(jpaPostRepository, times(1)).save(any(Post.class));
        verify(jpaPostImageRepository, times(1)).save(any(PostImage.class));
    }

    @Test
    @DisplayName("게시글 작성 성공 - 이미지 미포함")
    void createPost_shouldReturnSuccess_whenImageIsEmpty() {
        // given
        PostCreateUpdateRequestDTO postCreateUpdateRequestDTO = new PostCreateUpdateRequestDTO(userId, postTitle, postContent, null);

        // when
        ResponseEntity<?> responseEntity = postService.createPost(postCreateUpdateRequestDTO);
        PostCreateUpdateResponseDTO postCreateUpdateResponseDTO = (PostCreateUpdateResponseDTO) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, postCreateUpdateResponseDTO.getCode());
        verify(jpaPostRepository, times(1)).save(any(Post.class));
        verify(jpaPostImageRepository, never()).save((any()));
    }

    @Test
    @DisplayName("게시글 작성 실패 - 서버 오류")
    void createPost_shouldReturnServerError_whenExceptionOccurs() {
        // given
        PostCreateUpdateRequestDTO postCreateUpdateRequestDTO = new PostCreateUpdateRequestDTO(userId, postTitle, postContent, null);
        doThrow(RuntimeException.class).when(jpaPostRepository).save(any(Post.class));

        // when
        ResponseEntity<?> responseEntity = postService.createPost(postCreateUpdateRequestDTO);
        ApiResponse  apiResponse = (ApiResponse) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, apiResponse.getCode());
        verify(jpaPostRepository, times(1)).save(any(Post.class));
    }

    @Test
    @DisplayName("게시글 반환 성공")
    void getPost_shouldReturnSuccess() {
        // given
        when(jpaPostRepository.findById(postId)).thenReturn(Optional.of(new Post(userId, postTitle, postContent)));
        when(jdbcMemberRepository.findEmailById(userId)).thenReturn(Optional.of(userEmail));

        // when
        ResponseEntity<?> responseEntity = postService.getPostById(postId);
        PostResponseDTO postResponseDTO = (PostResponseDTO) responseEntity.getBody();

        assertEquals(ResponseCode.SUCCESS, postResponseDTO.getCode());
        assertEquals(postTitle, postResponseDTO.getPost_title());
        assertEquals(userEmail, postResponseDTO.getPost_writer());
        verify(jpaPostRepository, times(1)).findById(postId);
        verify(jdbcMemberRepository, times(1)).findEmailById(userId);
    }

    @Test
    @DisplayName("게시글 반환 실패 - 없는 게시글")
    void getPost_shouldReturnNotFoundPost() {
        // given
        when(jpaPostRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        ResponseEntity<?> responseEntity = postService.getPostById(postId);
        ApiResponse  apiResponse = (ApiResponse) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.NOT_EXISTED_POST, apiResponse.getCode());
        verify(jpaPostRepository, times(1)).findById(postId);
        verify(jdbcMemberRepository, never()).findEmailById(anyInt());
    }

    @Test
    @DisplayName("게시글 반환 실패 - 서버 오류")
    void getPost_shouldReturnServerError_whenExceptionOccurs() {
        // given
        when(jpaPostRepository.findById(postId)).thenReturn(Optional.of(new Post(userId, postTitle, postContent)));
        doThrow(RuntimeException.class).when(jdbcMemberRepository).findEmailById(userId);

        // when
        ResponseEntity<?> responseEntity = postService.getPostById(postId);
        ApiResponse  apiResponse = (ApiResponse) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, apiResponse.getCode());
        verify(jpaPostRepository, times(1)).findById(postId);
        verify(jdbcMemberRepository, times(1)).findEmailById(userId);
    }

    @Test
    @DisplayName("댓글 작성 성공")
    void createPostComment_shouldReturnSuccess() {
        // given
        PostCommentCreateUpdateRequestDTO postCommentCreateUpdateRequestDTO = new PostCommentCreateUpdateRequestDTO(userId, commentContent);
        when(jpaPostRepository.findById(postId)).thenReturn(Optional.of(new Post(userId, postTitle, postContent)));

        // when
        ResponseEntity<?> responseEntity = postService.createPostComment(postCommentCreateUpdateRequestDTO, postId);
        PostCommentCreateUpdateResponseDTO postCommentCreateResponseDTO = (PostCommentCreateUpdateResponseDTO) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, postCommentCreateResponseDTO.getCode());
        verify(jpaPostRepository, times(1)).findById(anyLong());
        verify(jpaPostCommentRepository, times(1)).save(any(PostComment.class));
    }

    @Test
    @DisplayName("댓글 작성 실패 - 없는 게시글")
    void createPostComment_shouldReturnNotFoundPost() {
        // given
        PostCommentCreateUpdateRequestDTO postCommentCreateUpdateRequestDTO = new PostCommentCreateUpdateRequestDTO(userId, commentContent);
        when(jpaPostRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        ResponseEntity<?> responseEntity = postService.createPostComment(postCommentCreateUpdateRequestDTO, postId);
        ApiResponse  apiResponse = (ApiResponse) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.NOT_EXISTED_POST, apiResponse.getCode());
        verify(jpaPostRepository, times(1)).findById(postId);
        verify(jpaPostCommentRepository, never()).save(any(PostComment.class));
    }

    @Test
    @DisplayName("댓글 작성 실패 - 서버 오류")
    void createPostComment_shouldReturnServerError_whenExceptionOccurs() {
        // given
        PostCommentCreateUpdateRequestDTO postCommentCreateUpdateRequestDTO = new PostCommentCreateUpdateRequestDTO(userId, commentContent);
        doThrow(RuntimeException.class).when(jpaPostRepository).findById(postId);

        // when
        ResponseEntity<?> responseEntity = postService.createPostComment(postCommentCreateUpdateRequestDTO, postId);
        ApiResponse  apiResponse = (ApiResponse) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, apiResponse.getCode());
        verify(jpaPostRepository, times(1)).findById(postId);
        verify(jpaPostCommentRepository, never()).save(any(PostComment.class));
    }

    @Test
    @DisplayName("이미지가 포함된 게시글 수정 성공 - 이미지 포함")
    void updatePost_shouldReturnSuccess_withImage_whenImageExists() {
        // given
        PostCreateUpdateRequestDTO postCreateUpdateRequestDTO = new PostCreateUpdateRequestDTO(userId, postTitle, postContent, postImageUrl);
        when(jpaPostRepository.findById(postId)).thenReturn(Optional.of(new Post(userId, postTitle, postContent)));
        when(jpaPostImageRepository.findByPostId(postId)).thenReturn(Optional.of(new PostImage(new Post(userId, postTitle, postContent), postImageUrl)));

        // when
        ResponseEntity<?> responseEntity = postService.updatePost(postCreateUpdateRequestDTO, postId);
        PostCreateUpdateResponseDTO postCreateResponseDTO = (PostCreateUpdateResponseDTO) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, postCreateResponseDTO.getCode());
        verify(jpaPostRepository, times(1)).findById(postId);
        verify(jpaPostImageRepository, times(1)).findByPostId(postId);
        verify(jpaPostImageRepository, never()).save(any(PostImage.class));
    }

    @Test
    @DisplayName("이미지가 미포함된 게시글 수정 성공 - 이미지 포함")
    void updatePost_shouldReturnSuccess_withImage_whenImageNotExists() {
        // given
        PostCreateUpdateRequestDTO postCreateUpdateRequestDTO = new PostCreateUpdateRequestDTO(userId, postTitle, postContent, postImageUrl);
        when(jpaPostRepository.findById(postId)).thenReturn(Optional.of(new Post(userId, postTitle, postContent)));
        when(jpaPostImageRepository.findByPostId(postId)).thenReturn(Optional.empty());

        // when
        ResponseEntity<?> responseEntity = postService.updatePost(postCreateUpdateRequestDTO, postId);
        PostCreateUpdateResponseDTO postCreateResponseDTO = (PostCreateUpdateResponseDTO) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, postCreateResponseDTO.getCode());
        verify(jpaPostRepository, times(1)).findById(postId);
        verify(jpaPostImageRepository, times(1)).findByPostId(postId);
        verify(jpaPostImageRepository, times(1)).save(any(PostImage.class));
    }

    @Test
    @DisplayName("이미지가 포함된 게시글 수정 실패 - 권한 문제")
    void updatePost_shouldReturnNotHavePermission_whenImageExists() {
        // given
        PostCreateUpdateRequestDTO postCreateUpdateRequestDTO = new PostCreateUpdateRequestDTO(wrongUserId, postTitle, postContent, postImageUrl);
        when(jpaPostRepository.findById(postId)).thenReturn(Optional.of(new Post(userId, postTitle, postContent)));

        // when
        ResponseEntity<?> responseEntity = postService.updatePost(postCreateUpdateRequestDTO, postId);
        ApiResponse  apiResponse = (ApiResponse) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.PERMITTED_ERROR, apiResponse.getCode());
        verify(jpaPostRepository, times(1)).findById(postId);
        verify(jpaPostImageRepository, never()).findById(anyLong());
        verify(jpaPostImageRepository, never()).save(any(PostImage.class));
    }

    @Test
    @DisplayName("이미지가 포함된 게시글 수정 실패 - 없는 게시글")
    void updatePost_shouldReturnNotFoundPost_whenImageExists() {
        // given
        PostCreateUpdateRequestDTO postCreateUpdateRequestDTO = new PostCreateUpdateRequestDTO(wrongUserId, postTitle, postContent, postImageUrl);
        when(jpaPostRepository.findById(postId)).thenReturn(Optional.empty());

        // when
        ResponseEntity<?> responseEntity = postService.updatePost(postCreateUpdateRequestDTO, postId);
        ApiResponse  apiResponse = (ApiResponse) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.NOT_EXISTED_POST, apiResponse.getCode());
        verify(jpaPostRepository, times(1)).findById(postId);
        verify(jpaPostImageRepository, never()).findById(anyLong());
        verify(jpaPostImageRepository, never()).save(any(PostImage.class));
    }

    @Test
    @DisplayName("이미지가 포함된 게시글 수정 실패 - 없는 게시글")
    void updatePost_shouldReturnServerError_whenImageExists_whenExceptionOccurs() {
        // given
        PostCreateUpdateRequestDTO postCreateUpdateRequestDTO = new PostCreateUpdateRequestDTO(wrongUserId, postTitle, postContent, postImageUrl);
        doThrow(RuntimeException.class).when(jpaPostRepository).findById(postId);

        // when
        ResponseEntity<?> responseEntity = postService.updatePost(postCreateUpdateRequestDTO, postId);
        ApiResponse  apiResponse = (ApiResponse) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, apiResponse.getCode());
        verify(jpaPostRepository, times(1)).findById(postId);
        verify(jpaPostImageRepository, never()).findById(anyLong());
        verify(jpaPostImageRepository, never()).save(any(PostImage.class));
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_shouldReturnSuccess() {
        // given
        PostCommentCreateUpdateRequestDTO requestDTO = new PostCommentCreateUpdateRequestDTO(userId, commentContent);
        PostComment existingComment = new PostComment(userId, new Post(userId, postTitle, postContent), commentContent);

        when(jpaPostCommentRepository.findById(anyLong())).thenReturn(Optional.of(existingComment));

        // when
        ResponseEntity<?> responseEntity = postService.updateComment(requestDTO, commentId);
        PostCommentCreateUpdateResponseDTO responseDTO = (PostCommentCreateUpdateResponseDTO) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.SUCCESS, responseDTO.getCode());
        verify(jpaPostCommentRepository, times(1)).findById(anyLong());
        verify(jpaPostCommentRepository, times(1)).save(any(PostComment.class));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 존재하지 않는 댓글")
    void updateComment_shouldReturnNotFound() {
        // given
        PostCommentCreateUpdateRequestDTO requestDTO = new PostCommentCreateUpdateRequestDTO(userId, commentContent);
        when(jpaPostCommentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        ResponseEntity<?> responseEntity = postService.updateComment(requestDTO, commentId);
        ApiResponse response = (ApiResponse) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.NOT_EXISTED_COMMENT, response.getCode());
        verify(jpaPostCommentRepository, times(1)).findById(anyLong());
        verify(jpaPostCommentRepository, never()).save(any(PostComment.class));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 권한 없음")
    void updateComment_shouldReturnNotHavePermission() {
        // given
        PostCommentCreateUpdateRequestDTO requestDTO = new PostCommentCreateUpdateRequestDTO(wrongUserId, commentContent);
        PostComment existingComment = new PostComment(userId, new Post(userId, postTitle, postContent), commentContent);

        when(jpaPostCommentRepository.findById(anyLong())).thenReturn(Optional.of(existingComment));

        // when
        ResponseEntity<?> responseEntity = postService.updateComment(requestDTO, commentId);
        ApiResponse response = (ApiResponse) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.PERMITTED_ERROR, response.getCode());
        verify(jpaPostCommentRepository, times(1)).findById(anyLong());
        verify(jpaPostCommentRepository, never()).save(any(PostComment.class));
    }

    @Test
    @DisplayName("댓글 수정 실패 - 서버 오류")
    void updateComment_shouldReturnServerError_whenExceptionOccurs() {
        // given
        PostCommentCreateUpdateRequestDTO requestDTO = new PostCommentCreateUpdateRequestDTO(userId, commentContent);
        doThrow(RuntimeException.class).when(jpaPostCommentRepository).findById(anyLong());

        // when
        ResponseEntity<?> responseEntity = postService.updateComment(requestDTO, commentId);
        ApiResponse response = (ApiResponse) responseEntity.getBody();

        // then
        assertEquals(ResponseCode.INTERNAL_SERVER_ERROR, response.getCode());
        verify(jpaPostCommentRepository, times(1)).findById(anyLong());
        verify(jpaPostCommentRepository, never()).save(any(PostComment.class));
    }

}
