package ee.markh.vaiki_backend.config;

import ee.markh.vaiki_backend.entity.Movie;
import ee.markh.vaiki_backend.repository.MovieRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

/**
 * Seeds a minimal set of movies for local development.
 * Activated with the "seed" profile.
 */
@Configuration
@Profile("seed")
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedMovies(
            MovieRepository movieRepository,
            @Value("${app.cloudfront.domain:}") String cloudfrontDomain) {
        return _ -> {
            if (movieRepository.count() > 0) {
                log.info("Movies already seeded, skipping...");
                return;
            }

            log.info("Seeding minimal movie set...");

            // If cloudfrontDomain is configured, build absolute poster/backdrop URLs.
            // Expect cloudfrontDomain like "d123abc.cloudfront.net" (no scheme, no trailing slash).
            String cfPrefix = (cloudfrontDomain == null || cloudfrontDomain.isBlank())
                    ? ""
                    : "https://" + cloudfrontDomain;

            var movies = List.of(
                    Movie.builder()
                            .slug("all-quiet-on-the-western-front-1930")
                            .title("All Quiet on the Western Front")
                            .year(1930)
                            .description("A young soldier faces profound disillusionment in the soul-destroying horror of World War I.")
                            .durationMinutes(152)
                            .posterUrl(cfPrefix.isEmpty()
                                    ? "https://images.unsplash.com/photo-1526392060635-9d6019884377?w=400&h=600&fit=crop"
                                    : cfPrefix + "/all-quiet-on-the-western-front-1930/poster.jpg")
                            .backdropUrl(cfPrefix.isEmpty()
                                    ? "https://images.unsplash.com/photo-1526392060635-9d6019884377?w=1920&h=1080&fit=crop"
                                    : cfPrefix + "/all-quiet-on-the-western-front-1930/backdrop.jpg")
                            .categories(List.of("Classics", "Anti-war"))
                            .director("Lewis Milestone")
                            .country("United States")
                            .hlsPath("/all-quiet-on-the-western-front-1930/master.m3u8")
                            .featured(true)                // <-- mark featured
                            .build(),

                    Movie.builder()
                            .slug("his-girl-friday")
                            .title("His Girl Friday")
                            .year(1940)
                            .description("A fast-talking reporter and her ex-husband mix love and news in this classic screwball comedy.")
                            .durationMinutes(92)
                            .posterUrl(cfPrefix.isEmpty()
                                    ? "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=400&h=600&fit=crop"
                                    : cfPrefix + "/his-girl-friday/poster.jpg")
                            .backdropUrl(cfPrefix.isEmpty()
                                    ? "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1920&h=1080&fit=crop"
                                    : cfPrefix + "/his-girl-friday/backdrop.jpg")
                            .categories(List.of("Classics", "Comedy"))
                            .director("Howard Hawks")
                            .country("United States")
                            .hlsPath("/his-girl-friday/master.m3u8")
                            .featured(false)
                            .build()
            );

            movieRepository.saveAll(movies);
            log.info("Seeded {} movies", movies.size());
        };
    }
}
