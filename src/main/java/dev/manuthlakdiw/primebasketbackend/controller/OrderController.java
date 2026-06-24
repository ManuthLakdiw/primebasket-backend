package dev.manuthlakdiw.primebasketbackend.controller;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import dev.manuthlakdiw.primebasketbackend.dto.order.CreateOrderRequest;
import dev.manuthlakdiw.primebasketbackend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@ApiController("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public String createOrder(
            Principal principal,
            @Valid @RequestBody CreateOrderRequest request) {
            return orderService.createOrder(principal.getName(), request);
    }
}
