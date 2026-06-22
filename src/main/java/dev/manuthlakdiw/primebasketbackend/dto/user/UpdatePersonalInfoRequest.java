package dev.manuthlakdiw.primebasketbackend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record UpdatePersonalInfoRequest(

        @NotBlank(message = "First name is required")
        String firstName,

        @NotBlank(message = "Last name is required")
        String lastName,

        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^\\+94\\s?\\d{2}\\s?\\d{3}\\s?\\d{4}$", message = "Phone number must be in +94 format (e.g. +94 77 123 4567)")
        String phoneNumber
) {
}
