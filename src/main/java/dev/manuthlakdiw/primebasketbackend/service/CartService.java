package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.cart.CartResponse;
import dev.manuthlakdiw.primebasketbackend.entity.CartEntity;

import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface CartService {
    CartResponse getCartByUserEmail(String email);
    CartResponse addToCart(String email, Long productId, int quantity);
    CartResponse updateQuantity(String email, Long cartItemId, int quantity);
    CartResponse removeFromCart(String email, Long cartItemId);
    void clearCart(String email);
}
