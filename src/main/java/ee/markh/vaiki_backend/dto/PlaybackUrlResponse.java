package ee.markh.vaiki_backend.dto;

import java.time.Instant;

/**
 * Response containing a signed playback URL and its expiration.
 */
public record PlaybackUrlResponse(
        String url,
        Instant expiresAt
) {}

