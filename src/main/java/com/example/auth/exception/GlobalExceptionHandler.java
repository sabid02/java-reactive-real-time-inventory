package com.example.auth.exception;

import com.example.auth.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Reactive Global Exception Handler (@RestControllerAdvice).
 * Intercepts application exceptions and formats them into clean, non-blocking Mono<ResponseEntity<ApiResponse<T>>> responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles duplicate user registration exceptions (HTTP 400 Bad Request).
     */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ApiResponse<Void> response = ApiResponse.error(
                "Registration Failed",
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage(),
                ex.getMessage()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    /**
     * Handles DTO validation errors (@Valid failures on RegisterRequest/LoginRequest) (HTTP 400 Bad Request).
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleValidationExceptions(WebExchangeBindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        ApiResponse<Void> response = ApiResponse.error(
                "Validation Failed",
                HttpStatus.BAD_REQUEST.value(),
                "Validation error occurred",
                errors
        );

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response));
    }

    /**
     * Handles invalid login credentials (HTTP 401 Unauthorized).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleBadCredentials(BadCredentialsException ex) {
        ApiResponse<Void> response = ApiResponse.error(
                "Authentication Failed",
                HttpStatus.UNAUTHORIZED.value(),
                "Invalid username or password",
                "Invalid username or password"
        );
        return Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response));
    }

    /**
     * Handles Redis connection failure errors (HTTP 503 Service Unavailable).
     */
    @ExceptionHandler(org.springframework.data.redis.RedisConnectionFailureException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleRedisException(org.springframework.data.redis.RedisConnectionFailureException ex) {
        String msg = "Redis connection error: Unable to reach Redis server on localhost:6379. Please ensure Redis is running.";
        ApiResponse<Void> response = ApiResponse.error(
                "Service Unavailable",
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                msg,
                msg
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    /**
     * Handles MongoDB connection and database resource errors (HTTP 503 Service Unavailable).
     */
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleDatabaseException(org.springframework.dao.DataAccessException ex) {
        String errorMessage = "Database connection error: Unable to reach database. Please try again.";
        if (ex.getMessage() != null && (ex.getMessage().contains("Redis") || ex.getCause() instanceof org.springframework.data.redis.RedisConnectionFailureException)) {
            errorMessage = "Redis connection error: Unable to reach Redis server on localhost:6379. Please ensure Redis is running.";
        } else if (ex.getMessage() != null && ex.getMessage().contains("Mongo")) {
            errorMessage = "Database connection error: Unable to reach MongoDB. Please try again.";
        }
        ApiResponse<Void> response = ApiResponse.error(
                "Database Error",
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                errorMessage,
                errorMessage
        );
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    /**
     * Catch-all handler for unexpected system exceptions (HTTP 500 Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<Void>>> handleGenericException(Exception ex) {
        ApiResponse<Void> response = ApiResponse.error(
                "Internal Server Error",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                ex.getMessage(),
                ex.getMessage()
        );
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response));
    }
}
