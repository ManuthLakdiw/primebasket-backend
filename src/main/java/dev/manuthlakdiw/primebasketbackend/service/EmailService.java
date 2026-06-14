package dev.manuthlakdiw.primebasketbackend.service;

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
}
