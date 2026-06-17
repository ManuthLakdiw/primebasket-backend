package dev.manuthlakdiw.primebasketbackend.controller;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import dev.manuthlakdiw.primebasketbackend.dto.category.CategoryRequest;
import dev.manuthlakdiw.primebasketbackend.dto.category.CategoryResponse;
import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.projection.CategoryDropdownProjection;
import dev.manuthlakdiw.primebasketbackend.service.CategoryService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */

@ApiController("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SecurityRequirement(name = "BearerAuth")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse createCategory(@Valid @RequestBody CategoryRequest request) {
        return categoryService.createCategory(request);
    }

    @SecurityRequirement(name = "BearerAuth")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryResponse updateCategory(@PathVariable long id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public String deleteCategory(@PathVariable long id) {
        return categoryService.deleteCategory(id);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public CategoryResponse getCategoryById(@PathVariable long id) {
        return categoryService.getCategoryById(id);
    }

    @GetMapping
    public PageResponse<CategoryResponse> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return categoryService.getAllCategories(page, size);
    }

    @SecurityRequirement(name = "BearerAuth")
    @GetMapping("/dropdown")
    @PreAuthorize("hasRole('ADMIN')")
    public List<CategoryDropdownProjection> getCategoriesForDropdown() {
        return categoryService.getCategoriesForDropdown();
    }
}
