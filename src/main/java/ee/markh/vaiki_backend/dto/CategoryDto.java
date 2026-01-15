package ee.markh.vaiki_backend.dto;

import ee.markh.vaiki_backend.entity.Category;

/**
 * Category DTO for API responses.
 */
public record CategoryDto(
        String slug,
        String title,
        int orderIndex
) {
    public static CategoryDto from(Category category) {
        return new CategoryDto(
                category.getSlug(),
                category.getTitle(),
                category.getOrderIndex()
        );
    }
}
