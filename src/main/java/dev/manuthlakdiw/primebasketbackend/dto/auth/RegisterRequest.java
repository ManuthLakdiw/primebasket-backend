package dev.manuthlakdiw.primebasketbackend.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */


public record RegisterRequest(
        @NotBlank(message = "First name is required" )
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name must only contain letters")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name must only contain letters")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String password

) {

    public String fullName() {
        return firstName + " " + lastName;
    }

    // Compact Constructor
    public RegisterRequest {
        if (email != null) {
            email = email.toLowerCase();
        }

        if (password.equals(email)) {
            throw new IllegalArgumentException("Password cannot be the same as email!");
        }

        firstName = capitalize(firstName);
        lastName = capitalize(lastName);
    }

    private String capitalize(String str) {
        if (str == null || str.isBlank()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}
