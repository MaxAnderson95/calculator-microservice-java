package tech.maxanderson.calculator.subtract.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ChaosException.class)
    public ResponseEntity<Map<String, String>> handleChaosException(ChaosException e) {
        log.error("ChaosException occurred: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "error", "Service Unavailable",
                        "message", e.getMessage(),
                        "type", "chaos_engineering"
                ));
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Map<String, String>> handleServiceException(ServiceException e) {
        log.error("ServiceException occurred: {}", e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        "error", "Bad Gateway",
                        "message", e.getMessage(),
                        "type", "downstream_service_error"
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e) {
        log.error("Validation error occurred: {}", e.getMessage());
        Map<String, Object> errors = new HashMap<>();
        errors.put("error", "Validation Failed");

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }
        errors.put("fields", fieldErrors);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception e) {
        log.error("Unexpected error occurred", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal Server Error",
                        "message", "An unexpected error occurred. Please try again later."
                ));
    }
}
