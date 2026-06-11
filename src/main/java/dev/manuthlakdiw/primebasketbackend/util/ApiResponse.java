package dev.manuthlakdiw.primebasketbackend.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean isSuccess;
    private int statusCode;
    private String message;
    private T data;
    private Object errors;

    @Builder.Default
    private String timestamp = LocalDateTime.now().toString();

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .statusCode(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .statusCode(201)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int statusCode, String message, Object errors) {
        return ApiResponse.<T>builder()
                .isSuccess(false)
                .statusCode(statusCode)
                .message(message)
                .errors(errors)
                .build();
    }

}
