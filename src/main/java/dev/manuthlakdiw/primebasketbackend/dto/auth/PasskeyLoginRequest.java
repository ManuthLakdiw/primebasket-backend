package dev.manuthlakdiw.primebasketbackend.dto.auth;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record PasskeyLoginRequest(
        Map<String, Object> credential
) {
}
