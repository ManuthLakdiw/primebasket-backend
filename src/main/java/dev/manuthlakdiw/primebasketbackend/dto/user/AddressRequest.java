package dev.manuthlakdiw.primebasketbackend.dto.user;

import dev.manuthlakdiw.primebasketbackend.entity.types.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record AddressRequest(
        @NotNull(message = "Address type is required")
        AddressType addressType,

        @NotBlank(message = "Street is required")
        String street,

        @NotBlank(message = "City is required")
        String city,

        @NotBlank(message = "District is required")
        String district,

        @NotBlank(message = "Postal code is required")
        String postalCode
) {
}
