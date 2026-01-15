package ee.markh.vaiki_backend.dto;

import ee.markh.vaiki_backend.entity.Movie;

import java.util.List;

/**
 * Movie DTO for API responses. Matches frontend Movie interface.
 */
public record MovieDto(
        String slug,
        String title,
        Integer year,
        String description,
        Integer durationMinutes,
        String posterUrl,
        String backdropUrl,
        List<String> categories,
        String director,
        String country,
        boolean featured,
        String featureText
) {
    public static MovieDto from(Movie movie) {
        return new MovieDto(
                movie.getSlug(),
                movie.getTitle(),
                movie.getYear(),
                movie.getDescription(),
                movie.getDurationMinutes(),
                movie.getPosterUrl(),
                movie.getBackdropUrl(),
                movie.getCategories(),
                movie.getDirector(),
                movie.getCountry(),
                movie.isFeatured(),
                movie.getFeatureText()
        );
    }
}
