package org.community.backend.dto.response.member;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MemberDeleteResponseDTO extends ApiResponse {
    private MemberDeleteResponseDTO() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<MemberDeleteResponseDTO> success() {
        MemberDeleteResponseDTO result = new MemberDeleteResponseDTO();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
