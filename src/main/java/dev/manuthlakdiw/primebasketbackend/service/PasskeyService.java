package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.auth.LoginResponse;
import dev.manuthlakdiw.primebasketbackend.dto.auth.PasskeyLoginRequest;
import dev.manuthlakdiw.primebasketbackend.dto.auth.PasskeyRegisterRequest;

import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

public interface PasskeyService {
    Map<String, Object> generateRegisterOptions(String email);
    void verifyAndSaveRegistration(String email, PasskeyRegisterRequest request);

    Map<String, Object> generateLoginOptions(String email);
    LoginResponse verifyLoginAndGenerateToken(PasskeyLoginRequest request);
}
