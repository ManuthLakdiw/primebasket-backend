package dev.manuthlakdiw.primebasketbackend.controller;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import dev.manuthlakdiw.primebasketbackend.dto.auth.*;
import dev.manuthlakdiw.primebasketbackend.dto.user.UserDetailResponse;
import dev.manuthlakdiw.primebasketbackend.service.AuthService;
import dev.manuthlakdiw.primebasketbackend.service.PasskeyService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@ApiController("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasskeyService passkeyService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDetailResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.registerUser(request);
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return authService.verifyOtp(request);
    }

    @PostMapping("/resend-otp")
    public ResendOtpResponse resendOtp(@Valid @RequestBody ResendOtpRequest request, HttpServletRequest servletRequest) {

        String ipAddress = servletRequest.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = servletRequest.getRemoteAddr();
        }

        return authService.resendOtp(request, ipAddress);

    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.authenticate(request);
    }

    @PostMapping("/refresh")
    public LoginResponse refreshAccessToken(@RequestBody @Valid RefreshTokenRequest request) {
        return authService.requestNewAccessToken(request);
    }

    @PostMapping("/google")
    public LoginResponse googleLogin(@RequestBody GoogleLoginRequest request) {
        return authService.googleLogin(request);
    }


    @PostMapping("/facebook")
    public LoginResponse facebookLogin(@RequestBody FacebookLoginRequest request) {
        return authService.facebookLogin(request);
    }


    @PostMapping(value = "/passkeys/login-options", params = "email")
    public Map<String, Object> getLoginOptions(@RequestParam String email) {
        return passkeyService.generateLoginOptions(email);
    }

    @PostMapping("/passkeys/login-verify")
    public LoginResponse verifyPasskeyLogin(@Valid @RequestBody PasskeyLoginRequest request) {
        return passkeyService.verifyLoginAndGenerateToken(request);
    }


}
