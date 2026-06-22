package dev.manuthlakdiw.primebasketbackend.controller;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import dev.manuthlakdiw.primebasketbackend.dto.auth.PasskeyRegisterRequest;
import dev.manuthlakdiw.primebasketbackend.dto.user.UpdatePasswordRequest;
import dev.manuthlakdiw.primebasketbackend.dto.user.UserDetailResponse;
import dev.manuthlakdiw.primebasketbackend.dto.user.UpdatePersonalInfoRequest;
import dev.manuthlakdiw.primebasketbackend.service.PasskeyService;
import dev.manuthlakdiw.primebasketbackend.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.security.Principal;
import java.util.Map;

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

}
