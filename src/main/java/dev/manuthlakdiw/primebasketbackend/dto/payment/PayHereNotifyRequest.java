package dev.manuthlakdiw.primebasketbackend.dto.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record PayHereNotifyRequest(
        @JsonProperty("merchant_id")
        @NotBlank(message = "Merchant ID is required")
        String merchantId,

        @JsonProperty("order_id")
        @NotBlank(message = "Order ID is required")
        String orderId,

        @JsonProperty("payhere_amount")
        @NotBlank(message = "Amount is required")
        String payhereAmount,

        @JsonProperty("payhere_currency")
        @NotBlank(message = "Currency is required")
        String payhereCurrency,

        @JsonProperty("status_code")
        @NotBlank(message = "Status code is required")
        String statusCode,

        @JsonProperty("md5sig")
        @NotBlank(message = "Signature is required")
        String md5sig
) {
}
