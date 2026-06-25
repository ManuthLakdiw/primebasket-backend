package dev.manuthlakdiw.primebasketbackend.dto.user;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record UserAdminResponse(
        String id,
        String firstName,
        String lastName,
        String email,
        String telephone,
        boolean isActivated,
        String initials
) {
}
