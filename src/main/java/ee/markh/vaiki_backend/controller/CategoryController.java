package ee.markh.vaiki_backend.controller;

import ee.markh.vaiki_backend.dto.CategoryDto;
import ee.markh.vaiki_backend.repository.CategoryRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryRepository categoryRepository;

    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * GET /api/categories - List all visible categories ordered by orderIndex
     */
    @GetMapping
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findByVisibleTrueOrderByOrderIndex()
                .stream()
                .map(CategoryDto::from)
                .toList();
    }
}
