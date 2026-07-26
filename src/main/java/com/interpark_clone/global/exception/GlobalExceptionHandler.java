package com.interpark_clone.global.exception;

import com.interpark_clone.global.code.BusinessErrorCode;
import com.interpark_clone.global.code.GeneralErrorCode;
import com.interpark_clone.global.response.Response;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Comparator;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== 커스텀 예외 ====================

    // 비즈니스 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Void>> handleBusinessException(BusinessException e) {
        BusinessErrorCode errorCode = e.getErrorCode();
        log.warn("BusinessException: {} - {}", errorCode.name(), e.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    // 일반 예외
    @ExceptionHandler(GeneralException.class)
    public ResponseEntity<Response<Void>> handleGeneralException(GeneralException e) {
        GeneralErrorCode errorCode = e.getErrorCode();
        log.warn("GeneralException: {} - {}", errorCode.name(), e.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    // JWT 인증 예외
    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<Response<Void>> handleJwtAuthenticationException(JwtAuthenticationException e) {
        GeneralErrorCode errorCode = e.getErrorCode();
        log.warn("JwtAuthenticationException: {} - {}", errorCode.name(), e.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    // ==================== Spring MVC 예외 ====================

    // @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        GeneralErrorCode errorCode = GeneralErrorCode.VALIDATION_ERROR;

        // 유효성 검증 메시지
        String message = e.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(ObjectError::getDefaultMessage)
                .orElse(errorCode.getMessage());

        log.warn("MethodArgumentNotValidException: {}", message);

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode, message));
    }

    // @ModelAttribute 검증 실패
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Response<Void>> handleBindException(BindException e) {
        GeneralErrorCode errorCode = GeneralErrorCode.VALIDATION_ERROR;

        String message = e.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(ObjectError::getDefaultMessage)
                .orElse(errorCode.getMessage());

        log.warn("BindException: {}", message);

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode, message));
    }

    // @RequestParam 검증 실패
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Response<Void>> handleConstraintViolationException(ConstraintViolationException e) {
        GeneralErrorCode errorCode = GeneralErrorCode.VALIDATION_ERROR;

        String message = e.getConstraintViolations()
                .stream()
                .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse(errorCode.getMessage());

        log.warn("ConstraintViolationException: {}", message);

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode, message));
    }

    // 필수 요청 파라미터 누락
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Response<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e
    ) {
        GeneralErrorCode errorCode = GeneralErrorCode.MISSING_REQUEST_PARAMETER;
        log.warn("MissingServletRequestParameterException: {}", e.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    // JSON 요청 본문 파싱 실패
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        GeneralErrorCode errorCode = GeneralErrorCode.INVALID_REQUEST_BODY;
        log.warn("HttpMessageNotReadableException: {}", e.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    // 요청 값 타입 변환 실패
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        GeneralErrorCode errorCode = GeneralErrorCode.TYPE_MISMATCH;
        log.warn("MethodArgumentTypeMismatchException: {}", e.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    // 지원하지 않는 HTTP 메서드
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e
    ) {
        GeneralErrorCode errorCode = GeneralErrorCode.METHOD_NOT_ALLOWED;
        log.warn("HttpRequestMethodNotSupportedException: {}", e.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    // 지원하지 않는 미디어 타입
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException e
    ) {
        GeneralErrorCode errorCode = GeneralErrorCode.UNSUPPORTED_MEDIA_TYPE;
        log.warn("HttpMediaTypeNotSupportedException: {}", e.getMessage());

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleException(Exception e) {
        GeneralErrorCode errorCode = GeneralErrorCode.INTERNAL_SERVER_ERROR;
        log.error("INTERNAL_SERVER_ERROR : ", e);

        return ResponseEntity.status(errorCode.getStatusCode()).body(Response.fail(errorCode));
    }
}
