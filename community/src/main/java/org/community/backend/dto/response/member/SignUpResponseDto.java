package org.community.backend.dto.response.member;

import lombok.Getter;
import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class SignUpResponseDto extends ApiResponse {
    private SignUpResponseDto() {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
    }

    public static ResponseEntity<SignUpResponseDto> success(){
        SignUpResponseDto result = new SignUpResponseDto();
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    public static ResponseEntity<ApiResponse> duplicateEmail(){
        ApiResponse result = new ApiResponse(ResponseCode.DUPLICATE_EMAIL, ResponseMessage.DUPLICATE_EMAIL);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }
}
