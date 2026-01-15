package ee.markh.vaiki_backend.controller;

import ee.markh.vaiki_backend.dto.MovieDto;
import ee.markh.vaiki_backend.dto.PlaybackUrlResponse;
import ee.markh.vaiki_backend.entity.Movie;
import ee.markh.vaiki_backend.repository.MovieRepository;
import ee.markh.vaiki_backend.service.CloudFrontSignerService;
import ee.markh.vaiki_backend.service.CloudFrontSignerService.SignedUrlResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieRepository movieRepository;
    private final CloudFrontSignerService cloudFrontSignerService;

    public MovieController(MovieRepository movieRepository, CloudFrontSignerService cloudFrontSignerService) {
        this.movieRepository = movieRepository;
        this.cloudFrontSignerService = cloudFrontSignerService;
    }

    /**
     * GET /api/movies - List all movies
     */
    @GetMapping
    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll()
                .stream()
                .map(MovieDto::from)
                .toList();
    }

    /**
     * GET /api/movies/{slug} - Get movie by slug
     */
    @GetMapping("/{slug}")
    public MovieDto getMovieBySlug(@PathVariable String slug) {
        Movie movie = movieRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: " + slug));
        return MovieDto.from(movie);
    }

    /**
     * GET /api/movies/{slug}/playback-url - Get signed CloudFront playback URL
     *
     * @param slug    Movie slug
     * @param ttl     Optional TTL in seconds (overrides default)
     */
    @GetMapping("/{slug}/playback-url")
    public PlaybackUrlResponse getPlaybackUrl(
            @PathVariable String slug,
            @RequestParam(required = false) Integer ttl) throws Exception {

        Movie movie = movieRepository.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Movie not found: " + slug));

        if (movie.getHlsPath() == null || movie.getHlsPath().isBlank()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No HLS content available for: " + slug);
        }

        SignedUrlResult result = cloudFrontSignerService.getSignedUrl(movie.getHlsPath(), ttl);

        return new PlaybackUrlResponse(result.url(), result.expiresAt());
    }

    @GetMapping("/api/featured")
    public ResponseEntity<MovieDto> getFeatured() {
        return movieRepository.findFirstByFeaturedTrue()
                .map(movie -> ResponseEntity.ok(MovieDto.from(movie)))
                .orElse(ResponseEntity.noContent().build());
    }

}

