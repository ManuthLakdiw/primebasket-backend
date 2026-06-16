package dev.manuthlakdiw.primebasketbackend.dto.category;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record CategoryResponse(
        long id,
        String name,
        String description,
        long productCount
) {
    public CategoryResponse(long id, String name, String description) {
        this(id, name, description, 0);
    }
}
