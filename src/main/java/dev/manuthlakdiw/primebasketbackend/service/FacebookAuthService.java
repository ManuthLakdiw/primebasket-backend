package dev.manuthlakdiw.primebasketbackend.service;

import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface FacebookAuthService {

    Map<String, Object> verifyToken(String accessToken);
}
