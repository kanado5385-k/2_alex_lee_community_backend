package org.community.backend.dto.response.member;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MemberInfChangeResponseDTO extends ApiResponse {
    private MemberInfChangeResponseDTO() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<MemberInfChangeResponseDTO> success() {
        MemberInfChangeResponseDTO result = new MemberInfChangeResponseDTO();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
