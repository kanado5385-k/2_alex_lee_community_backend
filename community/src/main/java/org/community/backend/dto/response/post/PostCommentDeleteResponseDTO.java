package org.community.backend.dto.response.post;


import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PostCommentDeleteResponseDTO extends ApiResponse {
    private PostCommentDeleteResponseDTO() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<PostCommentDeleteResponseDTO> success() {
        PostCommentDeleteResponseDTO result = new PostCommentDeleteResponseDTO();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ApiResponse> notHavePermission() {
        ApiResponse response = new ApiResponse(ResponseCode.PERMITTED_ERROR, ResponseMessage.PERMITTED_ERROR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
