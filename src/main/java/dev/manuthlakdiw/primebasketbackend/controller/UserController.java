package dev.manuthlakdiw.primebasketbackend.controller;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import dev.manuthlakdiw.primebasketbackend.dto.auth.PasskeyRegisterRequest;
import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.dto.user.*;
import dev.manuthlakdiw.primebasketbackend.entity.types.AddressType;
import dev.manuthlakdiw.primebasketbackend.service.PasskeyService;
import dev.manuthlakdiw.primebasketbackend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@ApiController("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasskeyService passkeyService;

    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/me")
    public UserDetailResponse getCurrentUser(Principal principal) {
        String email = principal.getName();
        return userService.getMyProfile(email);
    }

    @PostMapping("/passkeys/register-options")
    public Map<String, Object> getRegisterOptions(Principal principal) {
        return passkeyService.generateRegisterOptions(principal.getName());
    }

    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/passkeys/register-verify")
    public String verifyPasskeyRegistration(
            Principal principal,
            @Valid @RequestBody PasskeyRegisterRequest request) {
        passkeyService.verifyAndSaveRegistration(principal.getName(), request);
        return "Passkey registered successfully!";
    }

    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/me")
    public String updatePersonalInfo(@Valid @RequestBody UpdatePersonalInfoRequest request, Principal principal){
        return userService.updatePersonalInfo(principal.getName(), request);
    }

    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/me/password")
    public String updatePassword(@Valid @RequestBody UpdatePasswordRequest request, Principal principal){
        return userService.updatePassword(principal.getName(), request);
    }

    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/passkeys/{id}")
    public String deletePasskey(@PathVariable String id, Principal principal) {
        passkeyService.deletePasskey(principal.getName(), id);
        return "Passkey deleted successfully";
    }

    @SecurityRequirement(name = "BearerAuth")
    @PostMapping("/addresses")
    public String addAddress(
            @Valid @RequestBody AddressRequest request,
            Principal principal) {
        userService.addAddress(principal.getName(), request);
        return "Address added successfully";
    }

    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/addresses")
    public String updateAddress(
            @Valid @RequestBody AddressRequest request,
            Principal principal) {

        userService.updateAddress(principal.getName(), request);
        return "Address updated successfully";
    }

    @SecurityRequirement(name = "BearerAuth")
    @DeleteMapping("/addresses/{addressType}")
    public String deleteAddress(
            @PathVariable AddressType addressType,
            Principal principal) {

        userService.deleteAddress(principal.getName(), addressType);
        return "Address deleted successfully";
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public PageResponse<UserAdminResponse> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return userService.getAllCustomerAccounts(page, size);
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public UserFullDetailResponse getUserDetails(@PathVariable UUID userId) {
        return userService.getUserFullDetails(userId);
    }

    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{userId}/toggle-status")
    public String toggleUserStatus(@PathVariable UUID userId) {
        userService.toggleUserActivation(userId);
        return "User status updated successfully";
    }



}
