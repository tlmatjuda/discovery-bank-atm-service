package com.discovery.atm.exception;

import com.discovery.atm.dto.ErrorResponseDto;
import com.discovery.atm.dto.ResultDto;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponseDto> handleApiException(ApiException exception) {
        return buildResponse(exception.getStatus(), exception.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ErrorResponseDto> handleValidationExceptions(Exception exception) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception exception) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }

    private ResponseEntity<ErrorResponseDto> buildResponse(HttpStatus status, String reason) {
        ResultDto result = new ResultDto(false, status.value(), reason);
        return ResponseEntity.status(status).body(new ErrorResponseDto(result));
    }
}
