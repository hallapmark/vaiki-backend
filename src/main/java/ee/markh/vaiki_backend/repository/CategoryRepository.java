package ee.markh.vaiki_backend.repository;

import ee.markh.vaiki_backend.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findByVisibleTrueOrderByOrderIndex();
}
