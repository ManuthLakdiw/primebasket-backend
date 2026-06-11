package dev.manuthlakdiw.primebasketbackend.service;

import dev.manuthlakdiw.primebasketbackend.dto.auth.AuthResponse;
import dev.manuthlakdiw.primebasketbackend.dto.auth.RegisterRequest;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface AuthService {

    AuthResponse registerUser(RegisterRequest request);
}
