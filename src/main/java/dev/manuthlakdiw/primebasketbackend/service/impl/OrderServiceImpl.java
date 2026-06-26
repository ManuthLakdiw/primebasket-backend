package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.dto.order.CreateOrderRequest;
import dev.manuthlakdiw.primebasketbackend.dto.order.OrderDetailsResponse;
import dev.manuthlakdiw.primebasketbackend.dto.order.OrderItemResponse;
import dev.manuthlakdiw.primebasketbackend.dto.order.OrderSummaryResponse;
import dev.manuthlakdiw.primebasketbackend.entity.*;
import dev.manuthlakdiw.primebasketbackend.entity.types.OrderStatusType;
import dev.manuthlakdiw.primebasketbackend.entity.types.PaymentStatusType;
import dev.manuthlakdiw.primebasketbackend.repository.*;
import dev.manuthlakdiw.primebasketbackend.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;


    @Override
    @Transactional
    @CacheEvict(value = "userCart", key = "#email")
    public String createOrder(String email, CreateOrderRequest request) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CartEntity cart = cartRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        List<CartItemEntity> selectedCartItems = cart.getItems().stream()
                .filter(item -> request.cartItemIds().contains(item.getId()))
                .toList();

        if (selectedCartItems.isEmpty()) {
            throw new RuntimeException("No valid items selected for checkout");
        }

        BigDecimal itemsTotal = BigDecimal.ZERO;
        BigDecimal deliveryFee = new BigDecimal("400.00");
        List<OrderItemEntity> orderItemsToSave = new ArrayList<>();

        String orderNumber = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        OrderEntity newOrder = OrderEntity.builder()
                .orderNumber(orderNumber)
                .user(user)
                .shippingAddress(request.shippingAddress())
                .status(OrderStatusType.PENDING)
                .paymentStatus(PaymentStatusType.PENDING)
                .itemsTotal(itemsTotal)
                .deliveryFee(deliveryFee)
                .finalTotal(deliveryFee)
                .build();

        OrderEntity savedOrder = orderRepository.save(newOrder);

        for (CartItemEntity cartItem : selectedCartItems) {
            ProductEntity product = cartItem.getProduct();

            if (product.getStockQuantity() < cartItem.getQuantity()) {
                throw new RuntimeException("Product '" + product.getName() + "' does not have enough stock.");
            }

            BigDecimal currentPrice = product.getSellingPrice();
            itemsTotal = itemsTotal.add(currentPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .order(savedOrder)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .priceAtPurchase(currentPrice)
                    .build();

            orderItemsToSave.add(orderItem);

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product);
        }

        orderItemRepository.saveAll(orderItemsToSave);

        savedOrder.setItemsTotal(itemsTotal);
        savedOrder.setDeliveryFee(deliveryFee);
        savedOrder.setFinalTotal(itemsTotal.add(deliveryFee));
        savedOrder.setOrderItems(orderItemsToSave);
        orderRepository.save(savedOrder);


        return orderNumber;
    }

    @Override
    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Colombo")
    @Transactional
    @Async
    @CacheEvict(value = {"adminOrders", "orderDetails"}, allEntries = true)
    public void cancelAbandonedOrders() {
        LocalDateTime fifteenMinsAgo = LocalDateTime.now().minusMinutes(15);

        List<OrderEntity> abandonedOrders = orderRepository.findByStatusAndPaymentStatusAndCreatedAtBefore(
                OrderStatusType.PENDING,
                PaymentStatusType.PENDING,
                fifteenMinsAgo
        );

        if (!abandonedOrders.isEmpty()) {
            log.info("Found {} abandoned orders. Rolling back stock...", abandonedOrders.size());

            for (OrderEntity order : abandonedOrders) {

                order.getOrderItems().forEach(orderItem -> {
                    ProductEntity product = orderItem.getProduct();
                    product.setStockQuantity(product.getStockQuantity() + orderItem.getQuantity());
                    productRepository.save(product);
                });

                order.setStatus(OrderStatusType.CANCELLED);
                order.setPaymentStatus(PaymentStatusType.FAILED);

                orderRepository.save(order);
                log.info("Order {} has been CANCELLED and stock restored.", order.getOrderNumber());
            }
        }
    }

    @Override
    @Cacheable(value = "adminOrders", key = "#status != null ? #status.name() + '_' + #page + '_' + #size : 'all_' + #page + '_' + #size")
    public PageResponse<OrderSummaryResponse> getOrders(OrderStatusType status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<OrderEntity> orderPage;

        if (status != null) {
            orderPage = orderRepository.findByStatus(status, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }

        Page<OrderSummaryResponse> dtoPage = orderPage.map(order -> new OrderSummaryResponse(
                order.getId().toString(),
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getUser().getFirstName() + " " + order.getUser().getLastName(),
                order.getFinalTotal(),
                order.getStatus()
        ));

        return PageResponse.from(dtoPage);
    }

    @Override
    @Cacheable(value = "orderDetails", key = "#orderId")
    public OrderDetailsResponse getOrderDetails(UUID orderId) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        return new OrderDetailsResponse(
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getItemsTotal(),
                order.getDeliveryFee(),
                order.getFinalTotal(),
                order.getShippingAddress(),
                order.getUser().getFirstName() + " " + order.getUser().getLastName(),
                order.getUser().getEmail(),
                order.getUser().getTelephone(),
                order.getOrderItems().stream().map(item -> new OrderItemResponse(
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getPriceAtPurchase(),
                        item.getPriceAtPurchase().multiply(BigDecimal.valueOf(item.getQuantity())),
                        item.getProduct().getImages().getFirst()
                )).toList()
        );
    }

    @Transactional
    @Override
    @CacheEvict(value = {"adminOrders", "orderDetails"}, allEntries = true)
    public void updateOrderStatus(UUID orderId, OrderStatusType newStatus, String reason) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatusType currentStatus = order.getStatus();

        if (currentStatus == OrderStatusType.CANCELLED) {
            throw new RuntimeException("Order is already cancelled and cannot be modified.");
        }
        if (currentStatus == OrderStatusType.DELIVERED) {
            throw new RuntimeException("Order is already delivered and cannot be modified.");
        }

        if (newStatus == OrderStatusType.PENDING || newStatus == OrderStatusType.PROCESSING) {
            throw new RuntimeException("Cannot manually revert status to PENDING or PROCESSING.");
        }

        order.setStatus(newStatus);

        if (newStatus == OrderStatusType.CANCELLED) {
            emailService.sendOrderCancellationAlert(order.getUser(), order, reason);
        } else if (newStatus == OrderStatusType.DELIVERED) {
            emailService.sendOrderDeliveredAlert(order.getUser(), order);
        }

        orderRepository.save(order);
    }
}
