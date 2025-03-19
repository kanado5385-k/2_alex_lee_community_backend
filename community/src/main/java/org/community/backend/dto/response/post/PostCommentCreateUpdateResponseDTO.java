package org.community.backend.dto.response.post;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PostCommentCreateUpdateResponseDTO extends ApiResponse {
    private PostCommentCreateUpdateResponseDTO() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<PostCommentCreateUpdateResponseDTO> success() {
        PostCommentCreateUpdateResponseDTO result = new PostCommentCreateUpdateResponseDTO();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ApiResponse> postNotFound() {
        ApiResponse response = new ApiResponse(ResponseCode.NOT_EXISTED_POST, ResponseMessage.NOT_EXISTED_POST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
