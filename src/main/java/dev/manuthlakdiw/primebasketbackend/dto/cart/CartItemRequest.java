package dev.manuthlakdiw.primebasketbackend.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record CartItemRequest(
        @NotNull(message = "Product ID is required")
        Long productId,

        @Min(value = 1, message = "Quantity must be at least 1")
        int quantity
) {}

