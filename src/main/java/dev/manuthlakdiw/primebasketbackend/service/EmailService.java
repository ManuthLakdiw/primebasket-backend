package dev.manuthlakdiw.primebasketbackend.service;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface EmailService {

    void sendRegistrationOtp(String toEmail, String firstName, String otp);

    void sendAccountLockedAlert(String toEmail, String firstName);
}
