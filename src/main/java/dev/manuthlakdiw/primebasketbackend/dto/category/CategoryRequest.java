package dev.manuthlakdiw.primebasketbackend.dto.category;

import jakarta.validation.constraints.NotBlank;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public record CategoryRequest(
        @NotBlank(message = "Category name is required")
        String name,

        String description
) {
    public CategoryRequest {
        if (name != null) {
            name = name.strip();
        }

        if (description != null) {
            description = description.strip();
        }
    }
}
