package org.community.backend.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Getter // 모든 필드에 대해 getter 자동 생성
@AllArgsConstructor // 모든 필드에 대해 생성자 자동 생성
public class ApiResponse { // 모든 응답에 대한 코드와 메시지 관리
    private String code;
    private String message;

    public ApiResponse() {
        this.code = ResponseCode.SUCCESS;
        this.message = ResponseMessage.SUCCESS;
    }

    public static ResponseEntity<ApiResponse> databaseError() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse(ResponseCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR));
    }

    public static ResponseEntity<ApiResponse> validationFailed() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(ResponseCode.BAD_REQUEST, ResponseMessage.BAD_REQUEST));
    }

    public static ResponseEntity<ApiResponse> notHavePermission() {
        ApiResponse response = new ApiResponse(ResponseCode.PERMITTED_ERROR, ResponseMessage.PERMITTED_ERROR);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}