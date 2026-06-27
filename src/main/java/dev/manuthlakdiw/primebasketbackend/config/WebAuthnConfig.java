package dev.manuthlakdiw.primebasketbackend.config;

import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import dev.manuthlakdiw.primebasketbackend.repository.PasskeyCredentialRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class WebAuthnConfig {

    @Bean
    public RelyingParty relyingParty(PasskeyCredentialRepository credentialRepository) {
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id("primebasket-three.vercel.app")
                .name("PrimeBasket")
                .build();

        return RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(credentialRepository)
                .origins(Set.of(
                        "http://localhost:3000",
                        "https://primebasket-three.vercel.app"
                ))
                .build();
    }
}