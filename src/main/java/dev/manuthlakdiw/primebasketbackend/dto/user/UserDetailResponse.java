package dev.manuthlakdiw.primebasketbackend.dto.user;

import dev.manuthlakdiw.primebasketbackend.entity.types.Address;
import lombok.Builder;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@Builder
public record UserDetailResponse(
        String email,
        String firstName,
        String lastName,
        String role,
        String telephone,
        String authProvider,
        List<PasskeyResponse> passkeys,
        List<Address> addresses
) {

}
