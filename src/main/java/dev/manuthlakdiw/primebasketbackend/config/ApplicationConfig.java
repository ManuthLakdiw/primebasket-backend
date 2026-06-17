package dev.manuthlakdiw.primebasketbackend.config;

import dev.manuthlakdiw.primebasketbackend.projection.UserSecurityProjection;
import dev.manuthlakdiw.primebasketbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService(){
        return username -> {
            UserSecurityProjection userSecurityProjection = userRepository.findSecuredUserByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));


            List<GrantedAuthority> grantedAuthorityList = List.of(
                    new SimpleGrantedAuthority("ROLE_"+userSecurityProjection.getRole().name())
            );

            String password = userSecurityProjection.getPassword() != null ? userSecurityProjection.getPassword() : "";

            return new User(
                    userSecurityProjection.getEmail(),
                    password,
                    true,  // 1. accountNonExpired
                    true,  // 2. credentialsNonExpired
                    true,  // 3. accountNonLocked
                    userSecurityProjection.isActivated(),
                    grantedAuthorityList
            );
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider daoAuthenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

}
