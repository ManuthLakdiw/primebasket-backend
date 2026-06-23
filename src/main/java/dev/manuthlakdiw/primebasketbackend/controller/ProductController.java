package dev.manuthlakdiw.primebasketbackend.controller;

import dev.manuthlakdiw.primebasketbackend.annotation.ApiController;
import dev.manuthlakdiw.primebasketbackend.dto.common.PageResponse;
import dev.manuthlakdiw.primebasketbackend.dto.product.ProductRequest;
import dev.manuthlakdiw.primebasketbackend.dto.product.ProductResponse;
import dev.manuthlakdiw.primebasketbackend.service.ProductService;
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

@ApiController("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ProductResponse createProduct(@Valid @RequestBody ProductRequest request) {
        return productService.createProduct(request);
    }

    @GetMapping
    public PageResponse<ProductResponse> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getAllProducts(page, size);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public ProductResponse updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public String deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/public/{id}")
    public ProductResponse getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }


    @PatchMapping(value = "/{id}/active", params = "status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public void updateActiveStatus(
            @PathVariable Long id,
            @RequestParam boolean status) {
        productService.updateProductActiveStatus(id, status);
    }

    @PatchMapping(value = "/{id}/featured", params = "status")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "BearerAuth")
    public void updateFeaturedStatus(
            @PathVariable Long id,
            @RequestParam boolean status
    ){
        productService.setProductAsFeatured(id, status);
    }

    @GetMapping("/public/category/{categoryId}")
    public PageResponse<ProductResponse> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return productService.getProductsByCategory(categoryId, keyword, page, size);
    }


    @GetMapping("/public/featured/preview")
    public List<ProductResponse> getTopFeaturedProducts() {
        return productService.getTopFeaturedProducts(4);
    }

    @GetMapping("/public/featured")
    public PageResponse<ProductResponse> getFeaturedProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getFeaturedProducts(keyword, page, size);
    }

    @GetMapping("/public/onsale/preview")
    public List<ProductResponse> getTopOnSaleProducts() {
        return productService.getTopOnSaleProducts(4);
    }

    @GetMapping("/public/onsale")
    public PageResponse<ProductResponse> getOnSaleProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.getOnSaleProducts(keyword, page, size);
    }

    @GetMapping("/public/search")
    public PageResponse<ProductResponse> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return productService.searchAllProducts(keyword, page, size);
    }

    

}
