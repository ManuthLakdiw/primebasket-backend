package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.auth.*;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface AuthService {

    UserDetailResponse registerUser(RegisterRequest request);

    String verifyOtp(VerifyOtpRequest request);

    ResendOtpResponse resendOtp(ResendOtpRequest request, String ipAddress);

    LoginResponse authenticate(LoginRequest request);
}
