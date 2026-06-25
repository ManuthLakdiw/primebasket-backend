package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.order.CreateOrderRequest;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface OrderService {

    String createOrder(String email, CreateOrderRequest request);

    void cancelAbandonedOrders();
}
