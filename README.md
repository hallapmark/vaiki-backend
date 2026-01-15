# Vaiki Backend

Spring Boot backend for the Vaiki movie streaming platform.

## Requirements

- Java 25
- PostgreSQL (local dev)
- AWS CloudFront private key (PKCS#8 PEM format)

## Configuration

1. Copy `dev.env.example` to `dev.env` and fill in values:

```bash
cp dev.env.example dev.env
```

2. Create a PostgreSQL database:

```bash
createdb vaiki
```

3. Configure CloudFront signing key:
   - Obtain your CloudFront key pair ID and private key from AWS Console
   - Save the private key file locally (do NOT commit it)
   - Update `dev.env` with the path to your private key

### Private Key Format

The AWS SDK v2 CloudFrontUtilities expects a **PKCS#8** PEM-formatted private key.

If you have a PKCS#1 key (starts with `-----BEGIN RSA PRIVATE KEY-----`), convert it:

```bash
openssl pkcs8 -topk8 -inform PEM -in pkcs1.key -out pkcs8.pem -nocrypt
```

The resulting file should start with `-----BEGIN PRIVATE KEY-----`.

## Running

### Using Maven wrapper

```bash
# Load env vars and run
set -a && source dev.env && set +a
./mvnw spring-boot:run
```

### Using IDE

Configure your IDE to load environment variables from `dev.env`, then run `VaikiBackendApplication`.

## API Endpoints

### Movies

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/movies` | List all movies |
| GET | `/api/movies/{slug}` | Get movie by slug |
| GET | `/api/movies/{slug}/playback-url` | Get signed CloudFront playback URL |

### Playback URL Query Parameters

| Parameter | Type | Description |
|-----------|------|-------------|
| `ttl` | integer | Optional. Override default URL TTL in seconds |

### Example Responses

**GET /api/movies**
```json
[
  {
    "slug": "metropolis",
    "title": "Metropolis",
    "year": 1927,
    "description": "...",
    "durationMinutes": 153,
    "posterUrl": "...",
    "backdropUrl": "...",
    "categories": ["Classics", "Silent Cinema"],
    "director": "Fritz Lang",
    "country": "Germany"
  }
]
```

**GET /api/movies/metropolis/playback-url**
```json
{
  "url": "https://d123abc.cloudfront.net/metropolis/master.m3u8?Expires=...&Signature=...&Key-Pair-Id=...",
  "expiresAt": "2026-01-15T14:30:00Z"
}
```

## Data Seeding

To seed sample movie data, include `seed` in your active profiles:

```
SPRING_PROFILES_ACTIVE=dev,seed
```

The seeder runs once on startup and only inserts data if the movies table is empty.

## Project Structure

```
src/main/java/ee/markh/vaiki_backend/
├── VaikiBackendApplication.java   # Main application
├── config/
│   ├── DataSeeder.java           # Sample data seeder (@Profile("seed"))
│   └── WebConfig.java            # CORS configuration
├── controller/
│   └── MovieController.java      # REST endpoints
├── dto/
│   ├── MovieDto.java             # Movie response DTO
│   └── PlaybackUrlResponse.java  # Signed URL response
├── entity/
│   └── Movie.java                # JPA entity
├── repository/
│   └── MovieRepository.java      # JPA repository
└── service/
    └── CloudFrontSignerService.java  # CloudFront URL signing
```

