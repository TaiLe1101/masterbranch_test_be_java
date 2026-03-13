package com.masterbranch_be_test.masterbranch_be_test_calendar_java.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.masterbranch_be_test.masterbranch_be_test_calendar_java.common.ResponseApi;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ResponseApi<Object>> handleApiException(ApiException ex) {
        return ResponseEntity
            .status(ex.getStatus())
            .body(new ResponseApi<>(ex.getStatus().value(), ex.getMessage(), null));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseApi<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ResponseApi<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseApi<Object>> handleConstraintViolation(ConstraintViolationException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ResponseApi<>(HttpStatus.BAD_REQUEST.value(), "Validation failed", ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseApi<Object>> handleNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ResponseApi<>(HttpStatus.BAD_REQUEST.value(), "Invalid request payload", null));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseApi<Object>> handleException(Exception ex) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(new ResponseApi<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error", null));
    }
}
