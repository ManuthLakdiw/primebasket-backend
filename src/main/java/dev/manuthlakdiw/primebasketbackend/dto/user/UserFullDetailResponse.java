package dev.manuthlakdiw.primebasketbackend.dto.user;

import dev.manuthlakdiw.primebasketbackend.entity.types.Address;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record UserFullDetailResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String telephone,
        boolean isActivated,
        String role,
        String authProvider,
        String lastLogin,
        String initials,
        List<Address> addresses,
        long totalOrders,
        double totalSpent
) {
}
