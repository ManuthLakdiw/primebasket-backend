package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.entity.EmailLogEntity;
import dev.manuthlakdiw.primebasketbackend.entity.OrderEntity;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import dev.manuthlakdiw.primebasketbackend.entity.types.MailStatusType;
import dev.manuthlakdiw.primebasketbackend.repository.EmailLogRepository;
import dev.manuthlakdiw.primebasketbackend.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final EmailLogRepository emailLogRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;


    @Async
    @Override
    public void sendRegistrationOtp(UserEntity userEntity, String otp) {
        String subject = "Your PrimeBasket Verification Code 🛒";
        String htmlContent = buildOtpTemplate(userEntity.getFirstName(), otp);

        sendHtmlEmail(userEntity, subject, htmlContent);
    }


    @Async
    @Override
    public void sendAccountLockedAlert(UserEntity userEntity) {
        String subject = "⚠️ Security Alert: Verification Paused - PrimeBasket";
        String htmlContent = buildAccountLockedTemplate(userEntity.getFirstName());

        sendHtmlEmail(userEntity, subject, htmlContent);
    }

    @Async
    @Override
    public void sendOrderConfirmation(UserEntity user, OrderEntity order) {
        String subject = "Order Confirmed - " + order.getOrderNumber() + " 🛒";
        String htmlContent = buildOrderConfirmationTemplate(user, order);
        sendHtmlEmail(user, subject, htmlContent);
    }

    private void sendHtmlEmail(UserEntity userEntity, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(userEntity.getEmail());
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            saveEmailLog(userEntity, subject, htmlContent, MailStatusType.SENT, null);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email to: " + userEntity.getEmail(), e);
        }
    }

    private String buildOrderConfirmationTemplate(UserEntity user, OrderEntity order) {
        StringBuilder itemsHtml = new StringBuilder();

        for (dev.manuthlakdiw.primebasketbackend.entity.OrderItemEntity item : order.getOrderItems()) {
            itemsHtml.append("""
                <tr>
                    <td style="padding: 10px; border-bottom: 1px solid #eeeeee;">%s</td>
                    <td style="padding: 10px; border-bottom: 1px solid #eeeeee; text-align: center;">%d</td>
                    <td style="padding: 10px; border-bottom: 1px solid #eeeeee; text-align: right;">Rs. %.2f</td>
                </tr>
            """.formatted(item.getProduct().getName(), item.getQuantity(), item.getPriceAtPurchase()));
        }

        return """
                <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f9f9f9; padding: 40px 20px;">
                    <div style="max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05);">
                        <div style="background-color: #F57224; padding: 25px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 24px;">Order Confirmed! 🎉</h1>
                        </div>
                        <div style="padding: 30px;">
                            <p style="color: #555555; font-size: 16px;">Hi <strong>%s</strong>,</p>
                            <p style="color: #555555; font-size: 16px;">Thank you for shopping with PrimeBasket. Your order <strong>#%s</strong> has been successfully placed.</p>
                            
                            <h3 style="color: #333333; margin-top: 30px; border-bottom: 2px solid #F57224; padding-bottom: 5px;">Order Details</h3>
                            <table style="width: 100%%; border-collapse: collapse; margin-top: 15px; color: #555555;">
                                <thead>
                                    <tr style="background-color: #f1f1f1;">
                                        <th style="padding: 10px; text-align: left;">Item</th>
                                        <th style="padding: 10px; text-align: center;">Qty</th>
                                        <th style="padding: 10px; text-align: right;">Price</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    %s
                                </tbody>
                                <tfoot>
                                    <tr>
                                        <td colspan="2" style="padding: 10px; text-align: right; font-weight: bold;">Subtotal:</td>
                                        <td style="padding: 10px; text-align: right;">Rs. %.2f</td>
                                    </tr>
                                    <tr>
                                        <td colspan="2" style="padding: 10px; text-align: right; font-weight: bold;">Delivery Fee:</td>
                                        <td style="padding: 10px; text-align: right;">Rs. %.2f</td>
                                    </tr>
                                    <tr style="font-size: 18px; color: #F57224;">
                                        <td colspan="2" style="padding: 10px; text-align: right; font-weight: bold;">Total:</td>
                                        <td style="padding: 10px; text-align: right; font-weight: bold;">Rs. %.2f</td>
                                    </tr>
                                </tfoot>
                            </table>

                            <h3 style="color: #333333; margin-top: 30px; border-bottom: 2px solid #F57224; padding-bottom: 5px;">Shipping Details</h3>
                            <p style="color: #555555; line-height: 1.6;">
                                <strong>%s</strong><br/>
                                %s, %s, %s<br/>
                                Phone: %s
                            </p>
                        </div>
                    </div>
                </div>
                """.formatted(
                user.getFirstName(),
                order.getOrderNumber(),
                itemsHtml.toString(),
                order.getItemsTotal(),
                order.getDeliveryFee(),
                order.getFinalTotal(),
                order.getShippingAddress().getAddressType(),
                order.getShippingAddress().getStreet(),
                order.getShippingAddress().getCity(),
                order.getShippingAddress().getPostalCode(),
                user.getTelephone()
        );
    }


    private String buildOtpTemplate(String firstName, String otp) {
        return """
                <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f9f9f9; padding: 40px 20px;">
                    <div style="max-width: 550px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05);">
                       \s
                        <div style="background-color: #F57224; padding: 25px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 28px; letter-spacing: 1px;">PrimeBasket</h1>
                        </div>
                       \s
                        <div style="padding: 40px 30px;">
                            <h2 style="color: #333333; margin-top: 0; font-size: 22px;">Verify Your Email Address</h2>
                            <p style="color: #555555; font-size: 16px; line-height: 1.6;">Hi <strong>%s</strong>,</p>
                            <p style="color: #555555; font-size: 16px; line-height: 1.6;">
                                Welcome to PrimeBasket! To complete your registration and start shopping, please use the verification code below.
                            </p>
                           \s
                            <div style="text-align: center; margin: 35px 0;">
                                <div style="display: inline-block; background-color: #fff4ed; border: 2px dashed #F57224; padding: 15px 40px; border-radius: 8px;">
                                    <span style="color: #F57224; font-size: 32px; font-weight: bold; letter-spacing: 8px;">
                                        %s
                                    </span>
                                </div>
                                <p style="color: #F57224; font-size: 13px; margin-top: 10px; font-weight: bold;">
                                    ⏰ Valid for exactly 2 minutes
                                </p>
                            </div>
                           \s
                            <p style="color: #777777; font-size: 14px; line-height: 1.5; border-top: 1px solid #eeeeee; padding-top: 20px;">
                                If you didn't create an account with PrimeBasket, you can safely ignore this email.
                            </p>
                        </div>
                       \s
                        <div style="background-color: #f1f1f1; padding: 15px; text-align: center;">
                            <p style="color: #999999; font-size: 12px; margin: 0;">
                                &copy; 2026 PrimeBasket. All rights reserved.
                            </p>
                        </div>
                       \s
                    </div>
                </div>
               \s""".formatted(firstName, otp);
    }

    
    private String buildAccountLockedTemplate(String firstName) {
        return """
                <div style="font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f9f9f9; padding: 40px 20px;">
                    <div style="max-width: 550px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.05);">
                       \s
                        <div style="background-color: #F57224; padding: 25px; text-align: center;">
                            <h1 style="color: #ffffff; margin: 0; font-size: 28px; letter-spacing: 1px;">PrimeBasket</h1>
                        </div>
                       \s
                        <div style="padding: 40px 30px;">
                            <h2 style="color: #D32F2F; margin-top: 0; font-size: 22px;">⚠️ Security Alert: Action Paused</h2>
                            <p style="color: #555555; font-size: 16px; line-height: 1.6;">Hi <strong>%s</strong>,</p>
                            <p style="color: #555555; font-size: 16px; line-height: 1.6;">
                                We noticed multiple recent attempts to request a verification code for your PrimeBasket account.\s
                                To protect your account from unauthorized access, we have temporarily paused the verification process.
                            </p>
                           \s
                            <div style="text-align: center; margin: 35px 0;">
                                <div style="display: inline-block; background-color: #ffebee; border: 2px dashed #D32F2F; padding: 15px 40px; border-radius: 8px;">
                                    <span style="color: #D32F2F; font-size: 20px; font-weight: bold; letter-spacing: 1px;">
                                        Paused for 30 Minutes
                                    </span>
                                </div>
                            </div>
                           \s
                            <p style="color: #555555; font-size: 15px; line-height: 1.6;">
                                <strong style="color: #333333;">Was this you?</strong><br/>
                                You don't need to do anything. Simply wait for 30 minutes, and you will be able to request a new code.
                            </p>
                           \s
                            <p style="color: #555555; font-size: 15px; line-height: 1.6; margin-top: 20px;">
                                <strong style="color: #333333;">Didn't request this?</strong><br/>
                                Your account is secure. Someone may have entered your email address by mistake. No action is required from you.
                            </p>
                           \s
                            <p style="color: #777777; font-size: 14px; line-height: 1.5; border-top: 1px solid #eeeeee; padding-top: 20px; margin-top: 30px;">
                                This is an automated security message. Please do not reply to this email.
                            </p>
                        </div>
                       \s
                        <div style="background-color: #f1f1f1; padding: 15px; text-align: center;">
                            <p style="color: #999999; font-size: 12px; margin: 0;">
                                &copy; 2026 PrimeBasket. All rights reserved.
                            </p>
                        </div>
                       \s
                    </div>
                </div>
               \s""".formatted(firstName);
    }

    private void saveEmailLog(UserEntity user, String subject, String body, MailStatusType status, String error) {
        EmailLogEntity log = EmailLogEntity.builder()
                .recipientEmail(user.getEmail())
                .subject(subject)
                .body(body)
                .status(status)
                .errorMessage(error)
                .user(user)
                .build();

        emailLogRepository.save(log);
    }

}

