package dev.manuthlakdiw.primebasketbackend.controller;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import dev.manuthlakdiw.primebasketbackend.dto.payment.PayHereNotifyRequest;
import dev.manuthlakdiw.primebasketbackend.dto.payment.PaymentResponse;
import dev.manuthlakdiw.primebasketbackend.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@ApiController("/payments")
@RequiredArgsConstructor
public class PaymentController {


    private final PaymentService paymentService;


    @GetMapping("/request/{orderId}")
    public PaymentResponse getPaymentData(@PathVariable String orderId) {
        return paymentService.preparePaymentRequest(orderId);
    }

    @PostMapping(value = "/notify", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String handleNotify(
            @RequestParam("merchant_id") String merchantId,
            @RequestParam("order_id") String orderId,
            @RequestParam("payhere_amount") String payhereAmount,
            @RequestParam("payhere_currency") String payhereCurrency,
            @RequestParam("status_code") String statusCode,
            @RequestParam("md5sig") String md5sig
    ) {
        PayHereNotifyRequest notifyRequest = new PayHereNotifyRequest(
                merchantId, orderId, payhereAmount, payhereCurrency, statusCode, md5sig
        );
        return paymentService.handlePayHereNotify(notifyRequest);
    }
}
