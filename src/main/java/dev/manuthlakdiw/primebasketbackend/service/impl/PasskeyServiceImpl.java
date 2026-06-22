package dev.manuthlakdiw.primebasketbackend.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.yubico.webauthn.*;
import com.yubico.webauthn.data.*;
import dev.manuthlakdiw.primebasketbackend.dto.auth.LoginResponse;
import dev.manuthlakdiw.primebasketbackend.dto.auth.PasskeyLoginRequest;
import dev.manuthlakdiw.primebasketbackend.dto.auth.PasskeyRegisterRequest;
import dev.manuthlakdiw.primebasketbackend.entity.PasskeyEntity;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import dev.manuthlakdiw.primebasketbackend.repository.PasskeyRepository;
import dev.manuthlakdiw.primebasketbackend.repository.UserRepository;
import dev.manuthlakdiw.primebasketbackend.service.PasskeyService;
import dev.manuthlakdiw.primebasketbackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class PasskeyServiceImpl implements PasskeyService {

    private final RelyingParty relyingParty;
    private final UserRepository userRepository;
    private final PasskeyRepository passkeyRepository;
    private final StringRedisTemplate redisTemplate;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> generateRegisterOptions(String email) {
        UserEntity user = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            ByteArray userHandle = new ByteArray(user.getId().toString().getBytes());

            PublicKeyCredentialCreationOptions options = relyingParty.startRegistration(
                    StartRegistrationOptions.builder()
                            .user(UserIdentity.builder()
                                    .name(user.getEmail())
                                    .displayName(user.getFirstName() + " " + user.getLastName())
                                    .id(userHandle)
                                    .build())
                            .build()
            );

            String wrappedJson = options.toCredentialsCreateJson();

            com.fasterxml.jackson.databind.JsonNode innerNode = objectMapper.readTree(wrappedJson).get("publicKey");
            String unwrappedJson = innerNode.toString();

            String redisKey = "PASSKEY_REG_REQ:" + email;
            redisTemplate.opsForValue().set(redisKey, unwrappedJson, 5, TimeUnit.MINUTES);

            return objectMapper.readValue(unwrappedJson, new TypeReference<Map<String, Object>>(){});

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate register options", e);
        }
    }
    @Override
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#email")
    public void verifyAndSaveRegistration(String email, PasskeyRegisterRequest request) {
        UserEntity user = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            String redisKey = "PASSKEY_REG_REQ:" + email;
            String savedOptionsJson = redisTemplate.opsForValue().get(redisKey);

            if (savedOptionsJson == null) {
                throw new RuntimeException("Registration session expired or invalid");
            }

            PublicKeyCredentialCreationOptions options = PublicKeyCredentialCreationOptions.fromJson(savedOptionsJson);

            String credJson = objectMapper.writeValueAsString(request.credential());
            PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> pkc =
                    PublicKeyCredential.parseRegistrationResponseJson(credJson);

            RegistrationResult result = relyingParty.finishRegistration(
                    FinishRegistrationOptions.builder()
                            .request(options)
                            .response(pkc)
                            .build()
            );

            PasskeyEntity passkey = PasskeyEntity.builder()
                    .credentialId(result.getKeyId().getId().getBase64Url())
                    .publicKey(result.getPublicKeyCose().getBytes())
                    .signCount(result.getSignatureCount())
                    .deviceName(request.deviceName())
                    .user(user)
                    .build();

            passkeyRepository.save(passkey);

            redisTemplate.delete(redisKey);

        } catch (Exception e) {
            throw new RuntimeException("Failed to verify registration: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> generateLoginOptions(String email) {
        try {
            AssertionRequest request = relyingParty.startAssertion(
                    StartAssertionOptions.builder()
                            .username(email)
                            .build()
            );

            String redisJson = objectMapper.writeValueAsString(request);
            String redisKey = "PASSKEY_LOG_REQ:" + email;
            redisTemplate.opsForValue().set(redisKey, redisJson, 5, TimeUnit.MINUTES);

            String browserJson = request.toCredentialsGetJson();
            com.fasterxml.jackson.databind.JsonNode optionsNode = objectMapper.readTree(browserJson).get("publicKey");
            String unwrappedJson = optionsNode.toString();

            return objectMapper.readValue(unwrappedJson, new TypeReference<Map<String, Object>>(){});

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to generate login options: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public LoginResponse verifyLoginAndGenerateToken(PasskeyLoginRequest request) {
        try {
            String credJson = objectMapper.writeValueAsString(request.credential());

            PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> pkc =
                    PublicKeyCredential.parseAssertionResponseJson(credJson);

            String credentialId = pkc.getId().getBase64Url();
            PasskeyEntity passkey = passkeyRepository.findByCredentialId(credentialId)
                    .orElseThrow(() -> new RuntimeException("Passkey not found for this device"));

            String email = passkey.getUser().getEmail();
            String redisKey = "PASSKEY_LOG_REQ:" + email;
            String savedRequestJson = redisTemplate.opsForValue().get(redisKey);

            if (savedRequestJson == null) {
                throw new RuntimeException("Login session expired or invalid");
            }

            AssertionRequest assertionRequest = objectMapper.readValue(savedRequestJson, AssertionRequest.class);

            AssertionResult result = relyingParty.finishAssertion(
                    FinishAssertionOptions.builder()
                            .request(assertionRequest)
                            .response(pkc)
                            .build()
            );

            if (result.isSuccess()) {
                long newCount = result.getSignatureCount();
                System.out.println("Old count: " + passkey.getSignCount() + ", New count: " + newCount);
                passkey.setSignCount(newCount);
                passkeyRepository.saveAndFlush(passkey);
                redisTemplate.delete(redisKey);

                String accessToken = jwtUtil.generateAccessToken(email);
                String refreshToken = jwtUtil.generateRefreshToken(email);

                UserEntity user = passkey.getUser();
                user.setLastLogin(java.time.LocalDateTime.now());

                userRepository.save(user);
                return new LoginResponse(accessToken, refreshToken);
            } else {
                throw new RuntimeException("Invalid passkey signature");
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to verify login: " + e.getMessage(), e);
        }
    }
}