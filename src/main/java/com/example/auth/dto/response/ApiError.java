package com.example.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standardized structure for error details in API responses.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiError {
    private Integer status;
    private String errorMessage;
    private String localizedMessage;
    private Object details;
}
