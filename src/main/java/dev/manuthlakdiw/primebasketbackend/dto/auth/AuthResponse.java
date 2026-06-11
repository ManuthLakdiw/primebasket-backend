package dev.manuthlakdiw.primebasketbackend.dto.auth;

import lombok.Builder;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Builder
public record AuthResponse(
        String email,
        String firstName,
        String lastName,
        String role
) {

}
