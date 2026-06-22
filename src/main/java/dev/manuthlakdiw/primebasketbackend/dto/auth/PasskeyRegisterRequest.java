package dev.manuthlakdiw.primebasketbackend.dto.auth;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record PasskeyRegisterRequest(
        Map<String, Object> credential,

        @NotBlank(message = "Device name is required")
        String deviceName
) {
}
