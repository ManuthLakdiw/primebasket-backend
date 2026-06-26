package dev.manuthlakdiw.primebasketbackend.dto.order;

import java.math.BigDecimal;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record OrderItemResponse(
        String productName,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal,
        String imageUrl
) {
}
