package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.payment.PayHereNotifyRequest;
import dev.manuthlakdiw.primebasketbackend.dto.payment.PaymentResponse;

import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface PaymentService  {

    PaymentResponse preparePaymentRequest(String orderId);

    String handlePayHereNotify(PayHereNotifyRequest notifyRequest);


}
