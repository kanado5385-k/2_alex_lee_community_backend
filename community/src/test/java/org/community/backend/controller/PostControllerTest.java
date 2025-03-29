package org.community.backend.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.domain.entity.Post;
import org.community.backend.domain.entity.PostComment;
import org.community.backend.domain.member.Member;
import org.community.backend.dto.request.post.PostCreateUpdateRequestDTO;
import org.community.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JpaPostRepository jpaPostRepository;

    @Autowired
    private JpaPostImageRepository jpaPostImageRepository;

    @Autowired
    private JdbcMemberRepository jdbcMemberRepository;

    @Autowired
    private JpaPostCommentRepository jpaPostCommentRepository;

    @Autowired
    private JpaPostLikeRepository jpaPostLikeRepository;

    private int memberId;
    private final String email = "test@example.com";
    private final String password = "123456";
    private final String nickname = "tester";

    private Long postId;
    private final String postTitle = "test post";
    private final String postContent = "test post content";
    private final String postImageUrl = "test post image url";

    private final String commentContent = "test comment content";
    private Long commentId;

    @BeforeEach
    public void setup() {
        jdbcMemberRepository.save(new Member(email, password, nickname ));
        Optional<Integer> optionalMemberId = jdbcMemberRepository.findIdByEmail(email);
        memberId = optionalMemberId.get();

        Post savedPost = jpaPostRepository.save(new Post((long) memberId, postTitle, postContent));
        postId = savedPost.getId();

        PostComment postComment = jpaPostCommentRepository.save(new PostComment((long) memberId, savedPost, commentContent));
        commentId = postComment.getId();
    }

    @Test
    @DisplayName("게시글 작성 성공 - 이미지 포함")
    void createPostSuccess_withImage() throws Exception {
        createPostSuccess((long) memberId, postTitle, postContent, postImageUrl);
    }

    @Test
    @DisplayName("게시글 작성 성공 - 이미지 미포함")
    void createPostSuccess_withoutImage() throws Exception {
        createPostSuccess((long) memberId, postTitle, postContent, null);
    }

    private void createPostSuccess(Long localMemberId, String localPostTitle, String localPostContent, String localPostImageUrl) throws Exception {
        PostCreateUpdateRequestDTO request = new PostCreateUpdateRequestDTO((long) localMemberId, localPostTitle, localPostContent, localPostImageUrl);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS));
    }

    @Test
    @DisplayName("게시글 반환 성공")
    void getPostSuccess() throws Exception {
        mockMvc.perform(get("/posts/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.SUCCESS))
                .andExpect(jsonPath("$.post_title").value(postTitle));
    }

    @Test
    @DisplayName("게시글 반환 실패 - 없는 게시글")
    void getPostNotFoundPost() throws Exception {
        Long wrongPostId = 1L;

        mockMvc.perform(get("/posts/{postId}", wrongPostId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ResponseCode.NOT_EXISTED_POST));
    }
}
