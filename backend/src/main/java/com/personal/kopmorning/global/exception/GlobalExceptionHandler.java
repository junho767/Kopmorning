package com.personal.kopmorning.global.exception;

import com.personal.kopmorning.global.entity.RsData;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<RsData<?>> handleServiceException(ServiceException ex) {
        RsData<?> rsData = new RsData<>(ex.getCode(), ex.getMessage(), null);
        return ResponseEntity
                .status(ex.getHttpStatus())
                .body(rsData);
    }
}