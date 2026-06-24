package dev.manuthlakdiw.primebasketbackend.controller;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import dev.manuthlakdiw.primebasketbackend.dto.cart.CartItemRequest;
import dev.manuthlakdiw.primebasketbackend.dto.cart.CartResponse;
import dev.manuthlakdiw.primebasketbackend.entity.CartEntity;
import dev.manuthlakdiw.primebasketbackend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@ApiController("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;



    @GetMapping
    public CartResponse getCart(Principal principal) {
        return cartService.getCartByUserEmail(principal.getName());
    }

    @PostMapping("/add")
    public CartResponse addToCart(Principal principal, @Valid @RequestBody CartItemRequest request) {
        return cartService.addToCart(principal.getName(), request.productId(), request.quantity());
    }

    @PutMapping("/update/{cartItemId}")
    public CartResponse updateQuantity(
            Principal principal,
            @PathVariable Long cartItemId,
            @RequestParam int quantity) {
        return cartService.updateQuantity(principal.getName(), cartItemId, quantity);
    }

    @DeleteMapping("/remove/{cartItemId}")
    public CartResponse removeFromCart(Principal principal, @PathVariable Long cartItemId) {
        return cartService.removeFromCart(principal.getName(), cartItemId);
    }

    @DeleteMapping("/clear")
    public String clearCart(Principal principal) {
        cartService.clearCart(principal.getName());
        return "Cart cleared successfully";
    }





}
