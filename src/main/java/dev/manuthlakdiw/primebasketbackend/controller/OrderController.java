package dev.manuthlakdiw.primebasketbackend.controller;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.dto.order.CreateOrderRequest;
import dev.manuthlakdiw.primebasketbackend.dto.order.OrderDetailsResponse;
import dev.manuthlakdiw.primebasketbackend.dto.order.OrderSummaryResponse;
import dev.manuthlakdiw.primebasketbackend.entity.types.OrderStatusType;
import dev.manuthlakdiw.primebasketbackend.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;

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

    @SecurityRequirement(name = "BearerAuth")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public String createOrder(
            Principal principal,
            @Valid @RequestBody CreateOrderRequest request) {
            return orderService.createOrder(principal.getName(), request);
    }

    @SecurityRequirement(name = "BearerAuth")
    @GetMapping(params = "status")
    @PreAuthorize("hasRole('ADMIN')")
    public PageResponse<OrderSummaryResponse> getOrders(
            @RequestParam(required = false) OrderStatusType status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.getOrders(status, page, size);
    }

    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderDetailsResponse getOrderDetails(@PathVariable UUID id) {
        return orderService.getOrderDetails(id);
    }


    @PatchMapping(value = "/{id}/status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public void updateOrderStatus(
            @PathVariable UUID id,
            @RequestParam OrderStatusType newStatus,
            @RequestParam(required = false) String reason) {

        orderService.updateOrderStatus(id, newStatus, reason);
    }

}
