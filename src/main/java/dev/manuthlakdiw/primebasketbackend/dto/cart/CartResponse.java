package dev.manuthlakdiw.primebasketbackend.dto.cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record CartResponse(
        Long cartId,
        BigDecimal totalPrice,
        int totalItems, 
        List<CartItemResponse> items
) {
}
