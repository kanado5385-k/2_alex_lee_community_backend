package org.community.backend.dto.response.member;

import lombok.Getter;
import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class SignInResponseDTO extends ApiResponse {
    private int userId;

    public SignInResponseDTO(int userId) {
        super(ResponseCode.SUCCESS, ResponseMessage.SUCCESS);
        this.userId = userId;
    }

    public static ResponseEntity<SignInResponseDTO> success(int userId) {
        SignInResponseDTO result = new SignInResponseDTO(userId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    public static ResponseEntity<ApiResponse> mismatchLoginInf() {
        ApiResponse response = new ApiResponse(ResponseCode.SIGN_IN_FAIL, ResponseMessage.SIGN_IN_FAIL);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
