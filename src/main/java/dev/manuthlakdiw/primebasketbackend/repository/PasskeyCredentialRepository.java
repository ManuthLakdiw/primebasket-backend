package dev.manuthlakdiw.primebasketbackend.repository;

import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RegisteredCredential;
import com.yubico.webauthn.data.ByteArray;
import com.yubico.webauthn.data.PublicKeyCredentialDescriptor;
import dev.manuthlakdiw.primebasketbackend.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PasskeyCredentialRepository implements CredentialRepository {

    private final PasskeyRepository passkeyRepository;
    private final UserRepository userRepository;

    @Override
    public Set<PublicKeyCredentialDescriptor> getCredentialIdsForUsername(String username) {
        UserEntity user = userRepository.findUserEntityByEmail(username).orElse(null);
        if (user == null) return Set.of();

        return passkeyRepository.findByUserId(user.getId()).stream()
                .map(passkey -> {
                    try {
                        return PublicKeyCredentialDescriptor.builder()
                                .id(ByteArray.fromBase64Url(passkey.getCredentialId()))
                                .build();
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing credential ID", e);
                    }
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Optional<ByteArray> getUserHandleForUsername(String username) {
        return userRepository.findUserEntityByEmail(username)
                .map(user -> new ByteArray(user.getId().toString().getBytes()));
    }

    @Override
    public Optional<String> getUsernameForUserHandle(ByteArray userHandle) {
        return Optional.empty();
    }

    @Override
    public Optional<RegisteredCredential> lookup(ByteArray credentialId, ByteArray userHandle) {
        return passkeyRepository.findByCredentialId(credentialId.getBase64Url())
                .map(passkey -> RegisteredCredential.builder()
                        .credentialId(credentialId)
                        .userHandle(userHandle)
                        .publicKeyCose(new ByteArray(passkey.getPublicKey()))
                        .signatureCount(passkey.getSignCount())
                        .build()
                );
    }

    @Override
    public Set<RegisteredCredential> lookupAll(ByteArray credentialId) {
        return passkeyRepository.findByCredentialId(credentialId.getBase64Url())
                .map(passkey -> {
                    ByteArray userHandle = new ByteArray(passkey.getUser().getId().toString().getBytes());
                    RegisteredCredential credential = RegisteredCredential.builder()
                            .credentialId(credentialId)
                            .userHandle(userHandle)
                            .publicKeyCose(new ByteArray(passkey.getPublicKey()))
                            .signatureCount(passkey.getSignCount())
                            .build();
                    return Set.of(credential);
                })
                .orElse(Set.of());
    }
}