package com.phasezero.catalog.exception;

import com.phasezero.catalog.responsedto.RestApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle unsupported media type (e.g., text/plain instead of application/json)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<RestApiResponse<Void>> handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex, WebRequest request) {

        String path = request.getDescription(false).replace("uri=", "");

        // Get unsupported content type safely
        String unsupportedType = ex.getContentType() != null ?
                ex.getContentType().toString() : "unknown type";

        // getSupportedMediaTypes() never returns null, so no need to check
        String supportedTypes = ex.getSupportedMediaTypes().toString();

        RestApiResponse<Void> response = RestApiResponse.<Void>builder()
                .status("error")
                .message(String.format(
                        "Unsupported media type '%s'. Supported content types: %s",
                        unsupportedType, supportedTypes))
                .data(null)
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE).body(response);
    }

    // Handle missing request body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RestApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {

        String path = request.getDescription(false).replace("uri=", "");
        String errorMessage = "Request body is required";

        // Check the specific error safely
        if (ex.getMessage() != null) {
            if (ex.getMessage().contains("Required request body is missing")) {
                errorMessage = "Request body is required";
            } else if (ex.getMessage().contains("JSON parse error")) {
                // Safely get root cause message
                Throwable rootCause = ex.getRootCause();
                String rootCauseMessage = rootCause != null ? rootCause.getMessage() : "Unknown error";
                errorMessage = "Invalid JSON format in request body: " + rootCauseMessage;
            } else {
                errorMessage = "Malformed JSON request: " + ex.getMessage();
            }
        }

        RestApiResponse<Void> response = RestApiResponse.<Void>builder()
                .status("error")
                .message(errorMessage)
                .data(null)
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        String path = request.getDescription(false).replace("uri=", "");

        RestApiResponse<Map<String, String>> response = RestApiResponse.<Map<String, String>>builder()
                .status("error")
                .message("Validation failed. " + ex.getErrorCount() + " error(s) found")
                .data(errors)
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<RestApiResponse<Void>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");

        RestApiResponse<Void> response = RestApiResponse.<Void>builder()
                .status("error")
                .message(ex.getMessage())
                .data(null)
                .path(path)
                .build();

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (ex.getMessage() != null && ex.getMessage().contains("already exists")) {
            httpStatus = HttpStatus.CONFLICT;
        }

        return ResponseEntity.status(httpStatus).body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<RestApiResponse<Void>> handleProductNotFoundException(
            ProductNotFoundException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");

        RestApiResponse<Void> response = RestApiResponse.<Void>builder()
                .status("error")
                .message(ex.getMessage())
                .data(null)
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestApiResponse<Void>> handleGlobalException(Exception ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", "");

        RestApiResponse<Void> response = RestApiResponse.<Void>builder()
                .status("error")
                .message("Internal server error: " + (ex.getMessage() != null ? ex.getMessage() : "Unknown error"))
                .data(null)
                .path(path)
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}