package dev.manuthlakdiw.primebasketbackend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record ResendOtpRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        String email
) {
}
