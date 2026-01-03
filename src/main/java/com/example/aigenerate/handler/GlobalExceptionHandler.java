// src/main/java/com/example/videogeneration/exception/GlobalExceptionHandler.java

package com.example.aigenerate.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器：统一处理控制器层异常，返回标准化错误响应
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // ==============================
    // 1. 处理 JSON 反序列化错误（最常见于 400 Bad Request）
    // ==============================
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        String message = "Invalid request body: ";

        // 尝试提取更具体的错误原因
        if (ex.getCause() instanceof InvalidFormatException invalidFormat) {
            String fieldName = extractFieldName(invalidFormat.getPath());
            String targetType = invalidFormat.getTargetType().getSimpleName();
            message += String.format("Field '%s' cannot accept value '%s'. Expected type: %s.",
                    fieldName, invalidFormat.getValue(), targetType);
        } else if (ex.getCause() instanceof MismatchedInputException mismatch) {
            String fieldName = extractFieldName(mismatch.getPath());
            message += "Missing required field or invalid structure at: " + fieldName;
        } else if (ex.getCause() instanceof JsonMappingException jsonMapEx) {
            message += "JSON structure error: " + jsonMapEx.getOriginalMessage();
        } else {
            message += ex.getMessage();
        }

        log.warn("Bad Request - {}", message, ex);
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    // ==============================
    // 2. 处理 @Valid 校验失败（JSR-303 / Bean Validation）
    // ==============================
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        String message = "Validation failed: " + errors;
        log.warn("Validation error - {}", message);
        return buildErrorResponse(message, HttpStatus.BAD_REQUEST, request);
    }

    // ==============================
    // 3. 处理自定义业务异常（可选）
    // ==============================
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        log.warn("Client error: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        log.error("Server error", ex);
        return buildErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    // ==============================
    // 4. 通用错误响应构建器
    // ==============================
    private ResponseEntity<Object> buildErrorResponse(
            String message,
            HttpStatus status,
            WebRequest request) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return new ResponseEntity<>(body, status);
    }

    // ==============================
    // 工具方法：从 Jackson 路径中提取字段名
    // ==============================
    private String extractFieldName(Iterable<JsonMappingException.Reference> path) {
        StringBuilder sb = new StringBuilder();
        for (JsonMappingException.Reference ref : path) {
            if (sb.length() > 0) sb.append(".");
            sb.append(ref.getFieldName() != null ? ref.getFieldName() : ref.toString());
        }
        return sb.length() > 0 ? sb.toString() : "root";
    }
}