package org.community.backend.dto.response.post;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.community.backend.dto.response.member.MemberInfChangeResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PostCreateResponseDTO extends ApiResponse {
    private PostCreateResponseDTO() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<PostCreateResponseDTO> success() {
        PostCreateResponseDTO result = new PostCreateResponseDTO();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
