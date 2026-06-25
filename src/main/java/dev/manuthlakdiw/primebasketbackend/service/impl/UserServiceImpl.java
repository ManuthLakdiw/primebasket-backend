package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.user.*;
import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.entity.OrderEntity;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import dev.manuthlakdiw.primebasketbackend.entity.types.Address;
import dev.manuthlakdiw.primebasketbackend.entity.types.AddressType;
import dev.manuthlakdiw.primebasketbackend.entity.types.RoleType;
import dev.manuthlakdiw.primebasketbackend.repository.UserRepository;
import dev.manuthlakdiw.primebasketbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

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
                .addresses(user.getAddresses())
                .build();
    }

    @Override
    @Cacheable(value = "allUsers", key = "#page + '_' + #size")
    public PageResponse<UserAdminResponse> getAllCustomerAccounts(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<UserEntity> userPage = userRepository.findAllByRole(RoleType.USER, pageable);

        Page<UserAdminResponse> responsePage = userPage.map(user -> new UserAdminResponse(
                user.getId().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getTelephone(),
                user.isActivated(),
                getInitials(user.getFirstName(), user.getLastName()))
        );

        return PageResponse.from(responsePage);
    }

    @Override
    @CacheEvict(value = {"userProfiles", "allUsers", "userFullDetails"}, allEntries = true)
    public String toggleUserActivation(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setActivated(!user.isActivated());
        userRepository.save(user);

        return user.getEmail();
    }

    @Override
    @Cacheable(value = "userFullDetails", key = "#userId")
    public UserFullDetailResponse getUserFullDetails(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long totalOrders = user.getOrders().size();
        double totalSpent = user.getOrders().stream()
                .map(OrderEntity::getFinalTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .doubleValue();

        return new UserFullDetailResponse(
                user.getId().toString(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getTelephone(),
                user.isActivated(),
                user.getRole().name(),
                user.getAuthProvider().name(),
                user.getLastLogin() != null ? user.getLastLogin().toString() : "Never Logged In",
                getInitials(user.getFirstName(), user.getLastName()),
                user.getAddresses(),
                totalOrders,
                totalSpent
        );
    }

    @Transactional
    @Override
    @CacheEvict(value = {"userProfiles", "allUsers", "userFullDetails"}, allEntries = true)
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

    @Override
    @Transactional
    @CacheEvict(value = {"userProfiles", "allUsers", "userFullDetails"}, allEntries = true)
    public void addAddress(String email, AddressRequest request) {
        UserEntity user = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Address> addresses = user.getAddresses();
        if (addresses == null) {
            addresses = new ArrayList<>();
        }

        boolean exists = addresses.stream()
                .anyMatch(a -> a.getAddressType() == request.addressType());

        if (exists) {
            throw new RuntimeException("An address of type " + request.addressType() + " already exists. Please update it instead.");
        }

        if (addresses.size() >= 3) {
            throw new RuntimeException("Maximum of 3 addresses allowed.");
        }

        addresses.add(Address.builder()
                .addressType(request.addressType())
                .street(request.street())
                .city(request.city())
                .district(request.district())
                .postalCode(request.postalCode())
                .build());

        user.setAddresses(addresses);
        userRepository.save(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"userProfiles", "allUsers", "userFullDetails"}, allEntries = true)
    public void updateAddress(String email, AddressRequest request) {
        UserEntity user = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Address> addresses = user.getAddresses();
        if (addresses == null || addresses.isEmpty()) {
            throw new RuntimeException("No addresses found to update.");
        }

        boolean isUpdated = false;

        for (int i = 0; i < addresses.size(); i++) {
            if (addresses.get(i).getAddressType() == request.addressType()) {
                addresses.set(i, Address.builder()
                        .addressType(request.addressType())
                        .street(request.street())
                        .city(request.city())
                        .district(request.district())
                        .postalCode(request.postalCode())
                        .build());
                isUpdated = true;
                break;
            }
        }

        if (!isUpdated) {
            throw new RuntimeException("Address of type " + request.addressType() + " not found to update.");
        }

        user.setAddresses(addresses);
        userRepository.save(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"userProfiles", "allUsers", "userFullDetails"}, allEntries = true)
    public void deleteAddress(String email, AddressType addressType) {
        UserEntity user = userRepository.findUserEntityByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Address> addresses = user.getAddresses();
        if (addresses == null || addresses.isEmpty()) {
            throw new RuntimeException("No addresses found to delete.");
        }

        boolean removed = addresses.removeIf(a -> a.getAddressType() == addressType);

        if (!removed) {
            throw new RuntimeException("Address of type " + addressType + " not found.");
        }

        user.setAddresses(addresses);
        userRepository.save(user); 
    }

    private String getInitials(String firstName, String lastName) {
        String init = "";
        if (firstName != null && !firstName.isEmpty()) {
            init += firstName.charAt(0);
        }
        if (lastName != null && !lastName.isEmpty()) {
            init += lastName.charAt(0);
        }
        return init.toUpperCase();
    }


}
