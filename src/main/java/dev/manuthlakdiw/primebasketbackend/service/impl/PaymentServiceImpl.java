package dev.manuthlakdiw.primebasketbackend.service.impl;
import dev.manuthlakdiw.primebasketbackend.dto.payment.PayHereNotifyRequest;
import dev.manuthlakdiw.primebasketbackend.dto.payment.PaymentResponse;
import dev.manuthlakdiw.primebasketbackend.entity.OrderEntity;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import dev.manuthlakdiw.primebasketbackend.entity.types.OrderStatusType;
import dev.manuthlakdiw.primebasketbackend.entity.types.PaymentStatusType;
import dev.manuthlakdiw.primebasketbackend.repository.CartRepository;
import dev.manuthlakdiw.primebasketbackend.repository.OrderRepository;
import dev.manuthlakdiw.primebasketbackend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final EmailServiceImpl emailService;
    private final CacheManager cacheManager;


    @Value("${payhere.merchant.id}")
    private String merchantId;

    @Value("${payhere.merchant.secret}")
    private String merchantSecret;

    @Override
    public PaymentResponse preparePaymentRequest(String orderId) {
        OrderEntity order =  orderRepository.findOrderEntitiesByOrderNumber(orderId).orElseThrow(
                () -> new RuntimeException("Order not found with id: " + orderId)
        );

        double amount = order.getFinalTotal().doubleValue();
        String currency = "LKR";

        String hash = generateHash(merchantId, order.getOrderNumber(), amount, currency);

        return new PaymentResponse(merchantId, order.getOrderNumber(), amount, currency, hash);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"adminOrders", "orderDetails", "reports"}, allEntries = true)
    public String handlePayHereNotify(PayHereNotifyRequest notifyRequest) {
        String merchantId = notifyRequest.merchantId();
        String orderId = notifyRequest.orderId();
        String payhereAmount = notifyRequest.payhereAmount();
        String payhereCurrency = notifyRequest.payhereCurrency();
        String statusCode = notifyRequest.statusCode();
        String md5sig = notifyRequest.md5sig();

        try {
            String merchantSecretMd5 = md5(merchantSecret).toUpperCase();
            String localSignature = md5(merchantId + orderId + payhereAmount + payhereCurrency + statusCode + merchantSecretMd5).toUpperCase();

            if (!localSignature.equals(md5sig)) {
                throw new RuntimeException("Security Error: Invalid Signature!");
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash calculation failed", e);
        }

        OrderEntity order = orderRepository.findOrderEntitiesByOrderNumber(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if ("2".equals(statusCode)) {
            order.setPaymentStatus(PaymentStatusType.PAID);
            order.setStatus(OrderStatusType.PROCESSING);

            UserEntity user = order.getUser();

            cartRepository.findByUserEmail(user.getEmail()).ifPresent(cart -> {

                List<Long> orderedProductIds = order.getOrderItems().stream()
                        .map(item -> item.getProduct().getId())
                        .toList();

                cart.getItems().removeIf(cartItem ->
                        orderedProductIds.contains(cartItem.getProduct().getId())
                );

                BigDecimal newCartTotal = cart.getItems().stream()
                        .map(item -> item.getProduct().getSellingPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                cart.setTotalPrice(newCartTotal);
                cartRepository.save(cart);

                Cache userCartCache = cacheManager.getCache("userCart");
                if (userCartCache != null) {
                    userCartCache.evict(user.getEmail());
                }
            });

            emailService.sendOrderConfirmation(user, order);

        } else {
            order.setPaymentStatus(PaymentStatusType.FAILED);
        }
        orderRepository.save(order);

        return "OK";
    }


    private String generateHash(String merchantId, String orderId, double amount, String currency) {
        try {
            String formattedAmount = String.format("%.2f", amount);

            String merchantSecretMd5 = md5(merchantSecret).toUpperCase();

            String fullString = merchantId + orderId + formattedAmount + currency + merchantSecretMd5;

            return md5(fullString).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("Hash calculation failed", e);
        }
    }

    private String md5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : messageDigest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}