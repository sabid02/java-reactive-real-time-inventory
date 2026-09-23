package com.example.auth.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Standardized generic wrapper for all API JSON responses.
 *
 * @param <T> Payload data type
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.ALWAYS)
public class ApiResponse<T> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private String message;
    private T data;
    private ApiError error;

    @Builder.Default
    private String timeStamp = LocalDateTime.now().format(FORMATTER);

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(data)
                .error(null)
                .timeStamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    public static <T> ApiResponse<T> error(String message, ApiError error) {
        return ApiResponse.<T>builder()
                .message(message)
                .data(null)
                .error(error)
                .timeStamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    public static <T> ApiResponse<T> error(String message, int status, String errorMessage, Object details) {
        ApiError apiError = ApiError.builder()
                .status(status)
                .errorMessage(errorMessage)
                .localizedMessage(errorMessage)
                .details(details)
                .build();

        return ApiResponse.<T>builder()
                .message(message)
                .data(null)
                .error(apiError)
                .timeStamp(LocalDateTime.now().format(FORMATTER))
                .build();
    }

    public static <T> ApiResponse<T> error(String errorMessage) {
        return error("Error", 500, errorMessage, errorMessage);
    }
}
