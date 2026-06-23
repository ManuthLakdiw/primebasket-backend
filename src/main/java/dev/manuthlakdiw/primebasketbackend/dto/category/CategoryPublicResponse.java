package dev.manuthlakdiw.primebasketbackend.dto.category;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record CategoryPublicResponse(
        long id,
        String name,
        String description,
        long productCount
) {
}
