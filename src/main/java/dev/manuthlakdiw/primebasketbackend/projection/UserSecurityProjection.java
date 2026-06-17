package dev.manuthlakdiw.primebasketbackend.projection;

import dev.manuthlakdiw.primebasketbackend.entity.types.RoleType;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface UserSecurityProjection {
    String getPassword();
    String getEmail();
    RoleType getRole();
    boolean isActivated();
}
