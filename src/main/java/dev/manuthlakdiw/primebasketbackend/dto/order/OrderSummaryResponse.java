package dev.manuthlakdiw.primebasketbackend.dto.order;

import dev.manuthlakdiw.primebasketbackend.entity.types.OrderStatusType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record OrderSummaryResponse(
        String id,
        String orderNumber,
        LocalDateTime orderDate,
        String customerName,
        BigDecimal total,
        OrderStatusType status
) {
}
