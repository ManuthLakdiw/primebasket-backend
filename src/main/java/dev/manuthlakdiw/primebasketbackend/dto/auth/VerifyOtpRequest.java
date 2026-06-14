package dev.manuthlakdiw.primebasketbackend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record VerifyOtpRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email is invalid")
        String email,

        @NotBlank(message = "OTP is required")
        @Pattern(regexp = "^\\d{4}$", message = "OTP must contain exactly 4 digits")
        String otp
) {
}
