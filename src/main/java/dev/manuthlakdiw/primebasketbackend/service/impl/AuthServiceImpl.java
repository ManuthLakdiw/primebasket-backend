package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.auth.*;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import dev.manuthlakdiw.primebasketbackend.repository.UserRepository;
import dev.manuthlakdiw.primebasketbackend.service.AuthService;
import dev.manuthlakdiw.primebasketbackend.service.EmailService;
import dev.manuthlakdiw.primebasketbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtService;


    @Override
    @Transactional
    public UserDetailResponse registerUser(RegisterRequest request) {
        Optional<UserEntity> existingUserOpt = userRepository.findUserEntityByEmail(request.email());

        if (existingUserOpt.isPresent()) {
            UserEntity existingUser = existingUserOpt.get();
            if (existingUser.isActivated()) {
                throw new RuntimeException("Email is already in use. Please login.");
            } else {
                throw new RuntimeException("An account with this email exists but is not verified. Please verify your email.");
            }
        }

        UserEntity user = UserEntity.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        UserEntity savedUser = userRepository.save(user);

        String otp = generateOtp();
        String redisKey = "OTP:" + savedUser.getEmail();
        redisTemplate.opsForValue().set(redisKey, otp, 2, TimeUnit.MINUTES);

        emailService.sendRegistrationOtp(user, otp);

        return UserDetailResponse.builder()
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole().name())
                .build();
    }

    @Transactional
    @Override
    public String verifyOtp(VerifyOtpRequest request) {
        UserEntity user = userRepository.findUserEntityByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found. Please register first."));

        if (user.isActivated()) {
            throw new RuntimeException("Account is already verified. Please login.");
        }

        String redisKey = "OTP:" + request.email();
        String savedOtp = redisTemplate.opsForValue().get(redisKey);

        if (savedOtp == null) {
            throw new RuntimeException("OTP has expired. Please request a new code.");
        }

        if (!savedOtp.equals(request.otp())) {
            throw new RuntimeException("Incorrect OTP. Please try again.");
        }

        user.setActivated(true);

        redisTemplate.delete(redisKey);

        return "Account successfully verified and activated!";
    }

    @Override
    public ResendOtpResponse resendOtp(ResendOtpRequest request, String ipAddress) {
        String email = request.email();

        String blockKey = "Block:ResendOTP:" + ipAddress;
        String countKey = "Count:ResendOTP:" + ipAddress;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(blockKey))) {
            Long expireTime = redisTemplate.getExpire(blockKey, TimeUnit.SECONDS);

            throw new RuntimeException("Too many requests from your network. Please try again after "
                    + expireTime + " seconds.");
        }

        UserEntity user = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found. Please register first."));

        if (user.isActivated()) {
            throw new RuntimeException("Account is already verified. Please login.");
        }

        Long count = redisTemplate.opsForValue().increment(countKey);

        if (count != null && count == 1) {
            redisTemplate.expire(countKey, 15, TimeUnit.MINUTES);
        }

        String newOtp = generateOtp();
        String otpKey = "OTP:" + email;
        redisTemplate.opsForValue().set(otpKey, newOtp, 2, TimeUnit.MINUTES);
        emailService.sendRegistrationOtp(user, newOtp);

        if (count != null && count >= 3) {
            redisTemplate.opsForValue().set(blockKey, "BLOCKED", 30, TimeUnit.MINUTES);

            redisTemplate.delete(countKey);

             emailService.sendAccountLockedAlert(user);

            return new ResendOtpResponse(
                    "Maximum attempts reached. A final OTP has been sent. Your network is now blocked for 30 minutes.",
                    false,
                    1800
            );
        }

        return new ResendOtpResponse(
                "A new OTP has been sent.",
                false,
                120
        );
    }

    @Override
    public LoginResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        String accessToken = jwtService.generateAccessToken(request.email());
        String refreshToken = jwtService.generateRefreshToken(request.email());

        return new LoginResponse(
                accessToken,
                refreshToken
        );
    }

    private String generateOtp() {
        Random random = new Random();
        int otpNumber = 1000 + random.nextInt(9000);
        return String.valueOf(otpNumber);
    }


}




