package dev.manuthlakdiw.primebasketbackend.dto.auth;

import jakarta.validation.constraints.NotBlank;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record FacebookLoginRequest(

        @NotBlank(message = "Access token is required")
        String accessToken
) {
}
