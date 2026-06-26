package dev.manuthlakdiw.primebasketbackend.dto.order;

import dev.manuthlakdiw.primebasketbackend.entity.types.Address;
import dev.manuthlakdiw.primebasketbackend.entity.types.OrderStatusType;
import dev.manuthlakdiw.primebasketbackend.entity.types.PaymentStatusType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record OrderDetailsResponse(
        String orderNumber,
        LocalDateTime orderDate,
        OrderStatusType status,
        PaymentStatusType paymentStatus,
        BigDecimal itemsTotal,
        BigDecimal deliveryFee,
        BigDecimal finalTotal,
        Address shippingAddress,
        String customerName,
        String customerEmail,
        String customerPhone,
        List<OrderItemResponse> items
) {
}
