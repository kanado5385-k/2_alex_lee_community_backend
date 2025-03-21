package org.community.backend.dto.response.post;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PostDeleteResponseDTO extends ApiResponse {
    private PostDeleteResponseDTO() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<PostDeleteResponseDTO> success() {
        PostDeleteResponseDTO result = new PostDeleteResponseDTO();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
