package org.community.backend.service;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.domain.entity.Post;
import org.community.backend.domain.entity.PostImage;
import org.community.backend.dto.request.post.PostCreateUpdateRequestDTO;
import org.community.backend.dto.response.post.PostCreateUpdateResponseDTO;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    private final Long userId = 1L;
    private final String postTitle = "test post";
    private final String postContent = "test post content";
    private final String postImageUrl = "test post image url";

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

}
