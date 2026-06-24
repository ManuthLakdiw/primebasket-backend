package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.order.CreateOrderRequest;
import dev.manuthlakdiw.primebasketbackend.entity.*;
import dev.manuthlakdiw.primebasketbackend.entity.types.OrderStatusType;
import dev.manuthlakdiw.primebasketbackend.repository.*;
import dev.manuthlakdiw.primebasketbackend.service.CartService;
import dev.manuthlakdiw.primebasketbackend.service.EmailService;
import dev.manuthlakdiw.primebasketbackend.service.OrderService;
import dev.manuthlakdiw.primebasketbackend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;


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

        cart.getItems().removeAll(selectedCartItems);
        BigDecimal newCartTotal = cart.getItems().stream()
                .map(item -> item.getProduct().getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(newCartTotal);
        cartRepository.save(cart);

        emailService.sendOrderConfirmation(user, savedOrder);

        return orderNumber;
    }
}
