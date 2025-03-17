package org.community.backend.common;

import org.community.backend.common.response.ApiResponse;
import org.community.backend.common.response.ResponseCode;
import org.community.backend.common.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 컨트롤러(@RestController)에서 발생하는 예외를 감지하여 공통적으로 처리 가능 (AOP 기반으로 동작)
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class) // @Valid가 적용된 DTO에서 입력값이 잘못된 경우 자동으로 이 핸들러가 실행
    public ResponseEntity<ApiResponse> handleValidationException(MethodArgumentNotValidException ex) {
        // MethodArgumentNotValidException 객체를 매개변수로 받아서 예외 정보를 가져올 수 있음
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse(ResponseCode.BAD_REQUEST, ResponseMessage.BAD_REQUEST));
    }
}
