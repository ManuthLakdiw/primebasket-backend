package dev.manuthlakdiw.primebasketbackend.dto.user;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record PasskeyResponse(
        String id,
        String credentialId,
        String deviceName,
        String createdAt 
) {
}
