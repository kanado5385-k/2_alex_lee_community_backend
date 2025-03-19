package org.community.backend.dto.response.post;

import lombok.Getter;
import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.community.backend.domain.post.Post;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Getter
public class PostResponseDTO extends ApiResponse {

    private Long post_id;

    private String post_title;

    private String post_content;

    private String post_writer;

    private int view_count;

    private int like_count;

    private int comment_count;

    private LocalDateTime post_date;

    public PostResponseDTO(Post post, String postWriter) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.post_id = post.getId();
        this.post_title = post.getTitle();
        this.post_content = post.getContent();
        this.post_writer = postWriter;
        this.view_count = post.getViews();
        this.like_count = post.getLikeCount();
        this.comment_count = post.getCommentCount();
        this.post_date = post.getCreatedAt();
    }

    public static ResponseEntity<PostResponseDTO> success(Post post, String postWriter) {
        PostResponseDTO result = new PostResponseDTO(post, postWriter);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ApiResponse> postNotFound() {
        ApiResponse response = new ApiResponse(ResponseCode.NOT_EXISTED_POST, ResponseMessage.NOT_EXISTED_POST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
