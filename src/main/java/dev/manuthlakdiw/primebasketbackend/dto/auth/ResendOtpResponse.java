package dev.manuthlakdiw.primebasketbackend.dto.auth;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record ResendOtpResponse(
        String message,
        boolean isEnableResendOtp,
        long cooldownSeconds
) {
}
