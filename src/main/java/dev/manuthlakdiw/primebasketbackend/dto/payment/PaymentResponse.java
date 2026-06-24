package dev.manuthlakdiw.primebasketbackend.dto.payment;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record PaymentResponse(
        String merchantId,
        String orderId,
        double amount,
        String currency,
        String hash
){
}
