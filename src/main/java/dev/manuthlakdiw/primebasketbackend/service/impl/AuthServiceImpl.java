package dev.manuthlakdiw.primebasketbackend.service.impl;

import dev.manuthlakdiw.primebasketbackend.dto.auth.AuthResponse;
import dev.manuthlakdiw.primebasketbackend.dto.auth.RegisterRequest;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import dev.manuthlakdiw.primebasketbackend.entity.types.AuthProviderType;
import dev.manuthlakdiw.primebasketbackend.entity.types.RoleType;
import dev.manuthlakdiw.primebasketbackend.repository.UserRepository;
import dev.manuthlakdiw.primebasketbackend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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


    @Override
    public AuthResponse registerUser(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email is already in use");
        }


        UserEntity user = UserEntity.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .authProvider(AuthProviderType.LOCAL)
                .build();

        UserEntity savedUser = userRepository.save(user);

        return AuthResponse.builder()
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .role(savedUser.getRole().name())
                .build();
    }
}
