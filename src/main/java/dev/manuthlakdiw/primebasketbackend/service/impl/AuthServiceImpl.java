package dev.manuthlakdiw.primebasketbackend.service.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import dev.manuthlakdiw.primebasketbackend.dto.auth.*;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import dev.manuthlakdiw.primebasketbackend.entity.types.AuthProviderType;
import dev.manuthlakdiw.primebasketbackend.entity.types.RoleType;
import dev.manuthlakdiw.primebasketbackend.repository.UserRepository;
import dev.manuthlakdiw.primebasketbackend.service.AuthService;
import dev.manuthlakdiw.primebasketbackend.service.EmailService;
import dev.manuthlakdiw.primebasketbackend.service.GoogleAuthService;
import dev.manuthlakdiw.primebasketbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
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
    private final GoogleAuthService googleAuthService;


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
    @Transactional
    public LoginResponse authenticate(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        UserEntity user = userRepository.findUserEntityByEmail(request.email()).orElseThrow(
                () -> new RuntimeException("Account not found. Please contact admin to reset your password.")
        );
        user.setLastLogin(LocalDateTime.now());

        String accessToken = jwtService.generateAccessToken(request.email());
        String refreshToken = jwtService.generateRefreshToken(request.email());

        return new LoginResponse(
                accessToken,
                refreshToken
        );
    }

    @Override
    public LoginResponse requestNewAccessToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        if (!jwtService.isRefreshTokenMathematicallyValid(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token. Please login again.");
        }


        String username = jwtService.extractUsernameFromRefreshToken(refreshToken);

        UserEntity user = userRepository.findUserEntityByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isActivated()) {
            throw new RuntimeException("User account is locked or deactivated.");
        }

        String newAccessToken = jwtService.generateAccessToken(username);

        return new LoginResponse(
                newAccessToken,
                refreshToken
        );
    }

    @Override
    @Transactional
    public LoginResponse googleLogin(GoogleLoginRequest request) {
        try {
            GoogleIdToken.Payload payload = googleAuthService.verifyToken(request.idToken());

            String email = payload.getEmail();
            String firstName = (String) payload.get("given_name");
            String lastName = (String) payload.get("family_name");
            String googleSubjectId = payload.getSubject();

            userRepository.findUserEntityByEmail(email)
                    .map(existingUser -> {
                        existingUser.setAuthProvider(AuthProviderType.GOOGLE);
                        existingUser.setAuthProviderId(googleSubjectId);
                        existingUser.setFirstName(firstName);
                        existingUser.setLastName(lastName);
                        existingUser.setLastLogin(LocalDateTime.now());
                        return existingUser;
                    })
                    .orElseGet(() -> {
                        UserEntity newUser = UserEntity.builder()
                                .email(email)
                                .firstName(firstName)
                                .lastName(lastName)
                                .authProvider(AuthProviderType.GOOGLE)
                                .authProviderId(googleSubjectId)
                                .isActivated(true)
                                .role(RoleType.USER)
                                .lastLogin(LocalDateTime.now())
                                .build();
                        return userRepository.save(newUser);
                    });

            String accessToken = jwtService.generateAccessToken(email);
            String refreshToken = jwtService.generateRefreshToken(email);

            return new LoginResponse(accessToken, refreshToken);

        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    };

    private String generateOtp() {
        Random random = new Random();
        int otpNumber = 1000 + random.nextInt(9000);
        return String.valueOf(otpNumber);
    }


}




