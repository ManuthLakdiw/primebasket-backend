package dev.manuthlakdiw.primebasketbackend.projection;

/**
 * @author manuthlakdiv
 * @email manuthlakdiv2006.com
 * @project primebasket-backend
 * @github https://github.com/ManuthLakdiw
 */
public interface PublicCategoryProjection {
    Long getId();

    String getName();

    String getDescription();

    Long getProductCount();
}
