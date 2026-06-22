package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.user.PasskeyResponse;
import dev.manuthlakdiw.primebasketbackend.dto.user.UpdatePasswordRequest;
import dev.manuthlakdiw.primebasketbackend.dto.user.UserDetailResponse;
import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.dto.user.UpdatePersonalInfoRequest;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import dev.manuthlakdiw.primebasketbackend.projection.UserSummaryProjection;
import dev.manuthlakdiw.primebasketbackend.repository.UserRepository;
import dev.manuthlakdiw.primebasketbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "userProfiles", key = "#email")
    public UserDetailResponse getMyProfile(String email) {
        UserEntity user = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PasskeyResponse> passkeyResponses = user.getPasskeys().stream()
                .map(pk -> new PasskeyResponse(
                        pk.getId().toString(),
                        pk.getCredentialId(),
                        pk.getDeviceName(),
                        pk.getCreatedAt() != null ? pk.getCreatedAt().toLocalDate().toString() : "Unknown"
                ))
                .toList();

        return UserDetailResponse.builder()
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .telephone(user.getTelephone())
                .authProvider(user.getAuthProvider().name())
                .passkeys(passkeyResponses)
                .build();
    }

    @Override
    public PageResponse<UserSummaryProjection> getAllCustomerAccounts(int page, int size) {
        return null;
    }

    @Transactional
    @Override
    @CacheEvict(value = "userProfiles", key = "#email")
    public String updatePersonalInfo(String email, UpdatePersonalInfoRequest request) {
        UserEntity userEntity = userRepository.findUserEntityByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        if(userRepository.existsByTelephone(request.phoneNumber()) &&
                !Objects.equals(userEntity.getTelephone(), request.phoneNumber())){
            throw new RuntimeException("Phone number already exists for another user");
        }

        userEntity.setFirstName(request.firstName());
        userEntity.setLastName(request.lastName());
        userEntity.setTelephone(request.phoneNumber());

        userRepository.save(userEntity);

        return "User profile updated successfully!";
    }

    @Override
    @Transactional
    @CacheEvict(value = "userProfiles", key = "#email")
    public String updatePassword(String email, UpdatePasswordRequest request) {
        UserEntity user = userRepository.findUserEntityByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );

        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.newPassword()));

        user.setActivated(false);


        return "Password updated successfully. Please login again and verify OTP.";
    }

}
