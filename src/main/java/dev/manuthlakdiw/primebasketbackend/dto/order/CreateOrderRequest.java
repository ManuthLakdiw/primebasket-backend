package dev.manuthlakdiw.primebasketbackend.dto.order;

import dev.manuthlakdiw.primebasketbackend.entity.types.Address;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record CreateOrderRequest(
        @NotEmpty(message = "Please select at least one item to order")
        List<Long> cartItemIds,

        @NotNull(message = "Shipping address is required")
        Address shippingAddress
) {
}
