package ee.markh.vaiki_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false)
    private String title;

    private Integer year;

    @Column(length = 2000)
    private String description;

    private Integer durationMinutes;

    private String posterUrl;

    private String backdropUrl;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "movie_categories", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "category")
    private List<String> categories;

    private String director;

    private String country;

    /**
     * S3/CloudFront object path for the HLS master playlist.
     * Example: "/metropolis/master.m3u8"
     */
    private String hlsPath;

    private boolean featured;

    /**
     * Optional text shown alongside the featured badge (e.g., "Public Domain Day 2026").
     * Only relevant when featured = true.
     */
    private String featureText;
}

