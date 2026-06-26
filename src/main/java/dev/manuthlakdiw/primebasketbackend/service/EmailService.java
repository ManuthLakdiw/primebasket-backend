package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.entity.OrderEntity;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface EmailService {

    void sendRegistrationOtp(UserEntity userEntity, String otp);

    void sendAccountLockedAlert(UserEntity userEntity);

    void sendOrderConfirmation(UserEntity user, OrderEntity order);

    void sendOrderCancellationAlert(UserEntity user, OrderEntity order, String reason);

    void sendOrderDeliveredAlert(UserEntity user, OrderEntity order);

}
