package dev.manuthlakdiw.primebasketbackend.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record LoginResponse(

        @NotBlank(message = "Access Token is required")
        String accessToken,

        @NotBlank(message = "Refresh Token is required")
        String refreshToken
) {
}
