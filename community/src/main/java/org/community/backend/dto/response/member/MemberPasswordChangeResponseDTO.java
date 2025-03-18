package org.community.backend.dto.response.member;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MemberPasswordChangeResponseDTO extends ApiResponse {
    private MemberPasswordChangeResponseDTO() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<MemberPasswordChangeResponseDTO> success() {
        MemberPasswordChangeResponseDTO result = new MemberPasswordChangeResponseDTO();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
