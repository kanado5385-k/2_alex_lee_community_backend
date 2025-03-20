package org.community.backend.dto.response.post;

import lombok.Getter;
import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.community.backend.domain.entity.PostComment;
import org.community.backend.dto.response.post.object.PostCommentObjectDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostCommentListResponseDTO extends ApiResponse {

    private List<PostCommentObjectDTO> commentList;

    public PostCommentListResponseDTO(List<PostComment> comments, List<String> writers) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.commentList = comments.stream()
                .map(comment -> new PostCommentObjectDTO(comment, writers.get(comments.indexOf(comment))))
                .collect(Collectors.toList());
    }

    public static ResponseEntity<PostCommentListResponseDTO> success(List<PostComment> comments, List<String> writers) {
        PostCommentListResponseDTO result = new PostCommentListResponseDTO(comments, writers);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ApiResponse> postNotFound() {
        ApiResponse response = new ApiResponse(ResponseCode.NOT_EXISTED_POST, ResponseMessage.NOT_EXISTED_POST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    public static ResponseEntity<ApiResponse> noAnyCommentFound() {
        ApiResponse response = new ApiResponse(ResponseCode.NO_ANY_COMMENT, ResponseMessage.NO_ANY_COMMENT);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}