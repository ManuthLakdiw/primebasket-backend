package dev.manuthlakdiw.primebasketbackend.dto.cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        String sku,
        List<String> images,
        BigDecimal unitPrice,
        int quantity,
        BigDecimal subTotal,
        int availableStock
) {
}
