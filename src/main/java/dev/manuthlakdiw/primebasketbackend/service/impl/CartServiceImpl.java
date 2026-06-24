package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.cart.CartItemResponse;
import dev.manuthlakdiw.primebasketbackend.dto.cart.CartResponse;
import dev.manuthlakdiw.primebasketbackend.entity.CartEntity;
import dev.manuthlakdiw.primebasketbackend.entity.CartItemEntity;
import dev.manuthlakdiw.primebasketbackend.entity.ProductEntity;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import dev.manuthlakdiw.primebasketbackend.repository.CartRepository;
import dev.manuthlakdiw.primebasketbackend.repository.ProductRepository;
import dev.manuthlakdiw.primebasketbackend.repository.UserRepository;
import dev.manuthlakdiw.primebasketbackend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


    @Override
    @Cacheable(value = "userCart", key = "#email", unless = "#result == null")
    @Transactional(readOnly = true)
    public CartResponse getCartByUserEmail(String email) {
        CartEntity cart = cartRepository.findByUserEmail(email).orElseGet(() -> createNewCart(email));
        return mapToResponse(recalculateCartTotal(cart));
    }

    @Override
    @Transactional
    @CachePut(value = "userCart", key = "#email")
    public CartResponse addToCart(String email, Long productId, int quantity) {
        CartEntity cart = cartRepository.findByUserEmail(email).orElseGet(() -> createNewCart(email));

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.isDeleted() || !product.isActive()) {
            throw new RuntimeException("This product is no longer available.");
        }

        Optional<CartItemEntity> existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst();

        if (existingItem.isPresent()) {
            CartItemEntity item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItemEntity newItem = CartItemEntity.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();
            cart.getItems().add(newItem);
        }

        return mapToResponse(recalculateCartTotal(cart));
    }

    @Override
    @Transactional
    @CachePut(value = "userCart", key = "#email")
    public CartResponse updateQuantity(String email, Long cartItemId, int quantity) {
        CartEntity cart = cartRepository.findByUserEmail(email).orElseGet(() -> createNewCart(email));

        CartItemEntity itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getId() == cartItemId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        if (quantity <= 0) {
            cart.getItems().remove(itemToUpdate);
        } else {
            itemToUpdate.setQuantity(quantity);
        }

        return mapToResponse(recalculateCartTotal(cart));
    }

    @Override
    @Transactional
    @CachePut(value = "userCart", key = "#email")
    public CartResponse removeFromCart(String email, Long cartItemId) {
        CartEntity cart = cartRepository.findByUserEmail(email).orElseGet(() -> createNewCart(email));
        cart.getItems().removeIf(item -> item.getId() == cartItemId);
        return mapToResponse(recalculateCartTotal(cart));
    }

    @Override
    @Transactional
    @CacheEvict(value = "userCart", key = "#email")
    public void clearCart(String email) {
        CartEntity cart = cartRepository.findByUserEmail(email).orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

    private CartEntity createNewCart(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        CartEntity cart = CartEntity.builder()
                .user(user)
                .items(new ArrayList<>())
                .totalPrice(BigDecimal.ZERO)
                .build();
        return cartRepository.save(cart);
    }

    private CartEntity recalculateCartTotal(CartEntity cart) {
        cart.getItems().removeIf(item ->
                item.getProduct().isDeleted() || !item.getProduct().isActive());

        BigDecimal total = BigDecimal.ZERO;
        for (CartItemEntity item : cart.getItems()) {
            BigDecimal price = item.getProduct().getSellingPrice();
            BigDecimal itemTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(itemTotal);
        }

        cart.setTotalPrice(total);
        return cartRepository.save(cart);
    }

    private CartResponse mapToResponse(CartEntity cart) {

        List<CartItemResponse> itemResponses = cart.getItems().stream().map(item -> {
            BigDecimal price = item.getProduct().getSellingPrice();
            BigDecimal subTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));

            return new CartItemResponse(
                    item.getId(),
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    item.getProduct().getSku(),
                    item.getProduct().getImages(),
                    price,
                    item.getQuantity(),
                    subTotal,
                    item.getProduct().getStockQuantity()
            );
        }).toList();

        int totalItemsCount = itemResponses.size();

        return new CartResponse(
                cart.getId(),
                cart.getTotalPrice(),
                totalItemsCount,
                itemResponses
        );
    }



}
