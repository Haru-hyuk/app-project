package com.flower.exception;

import com.flower.dto.common.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /* =========================
       CustomException
       ========================= */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        ErrorCode code = e.getErrorCode();
        return buildResponse(code.getStatus(), code.getMessage());
    }

    /* =========================
       JWT 만료 / 유효성 오류
       ========================= */
    @ExceptionHandler({ExpiredJwtException.class, JwtException.class})
    public ResponseEntity<ErrorResponse> handleJwtException(Exception e) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");
    }

    /* =========================
       Validation 오류
       ========================= */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return buildResponse(HttpStatus.BAD_REQUEST, msg);
    }

    /* =========================
       JSON 파싱 오류
       ========================= */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "요청 데이터 형식이 올바르지 않습니다. JSON 형식을 확인해주세요."
        );
    }

    /* =========================
       RuntimeException (fallback)
       ========================= */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        return buildResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /* =========================
       그 외 모든 예외
       ========================= */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버 오류가 발생했습니다."
        );
    }

    /* =========================
       공통 Response 생성
       ========================= */
    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .status(status.value())
                                .message(message)
                                .build()
                );
    }
}
