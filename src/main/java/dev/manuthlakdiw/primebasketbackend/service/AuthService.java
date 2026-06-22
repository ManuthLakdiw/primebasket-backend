package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.auth.*;
import dev.manuthlakdiw.primebasketbackend.dto.user.UserDetailResponse;

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

    LoginResponse requestNewAccessToken(RefreshTokenRequest request);

    LoginResponse googleLogin(GoogleLoginRequest request);

    LoginResponse facebookLogin(FacebookLoginRequest request);
}
