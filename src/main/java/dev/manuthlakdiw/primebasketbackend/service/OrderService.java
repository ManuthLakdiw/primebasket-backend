package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.dto.order.CreateOrderRequest;
import dev.manuthlakdiw.primebasketbackend.dto.order.OrderDetailsResponse;
import dev.manuthlakdiw.primebasketbackend.dto.order.OrderSummaryResponse;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import dev.manuthlakdiw.primebasketbackend.entity.types.OrderStatusType;

import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface OrderService {

    String createOrder(String email, CreateOrderRequest request);

    void cancelAbandonedOrders();

    PageResponse<OrderSummaryResponse> getOrders(OrderStatusType status, int page, int size);

    OrderDetailsResponse getOrderDetails(UUID orderId);

    void updateOrderStatus(UUID orderId, OrderStatusType newStatus, String reason);

    PageResponse<OrderSummaryResponse> getMyOrders(String email, int page, int size);

    void cancelOrder(UUID orderId, String email);

}
