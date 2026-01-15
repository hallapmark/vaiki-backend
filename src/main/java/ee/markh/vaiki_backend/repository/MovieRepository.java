package ee.markh.vaiki_backend.repository;

import ee.markh.vaiki_backend.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findBySlug(String slug);
    Optional<Movie> findFirstByFeaturedTrue();


    boolean existsBySlug(String slug);
}

