# Vaiki Retro Films Streamer — Backend

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3-6DB33F?logo=springboot&logoColor=white)
![Java](https://img.shields.io/badge/Java-25-007396?logo=java&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4479A1?logo=postgresql&logoColor=white)

Spring Boot backend for a demo adaptive bitrate streaming platform showcasing secure video delivery for a retro/classic film library. Built as a portfolio project to demonstrate full-stack streaming architecture with AWS media delivery.

> 🎬 **[Live Demo on Amplify](https://main.d1kv8xolprj9bu.amplifyapp.com)** · 📦 **[Frontend Repository](https://github.com/hallapmark/vaiki-frontend)**

---

## Project Overview

This backend provides REST APIs for movie metadata and generates time-limited signed URLs for secure HLS video streaming from AWS CloudFront. It handles the business logic layer between the React frontend and AWS infrastructure.

### System Architecture

```
┌─────────────────┐                 ┌──────────────────┐
│                 │                 │                  │
│  React Frontend │◀────────────────│  Backend API     │
│                 │  Signed URLs    │  (Render)        │
│                 │                 │                  │
└────────┬────────┘                 └──────────────────┘
         │
         │ HLS video streams
         │ (using signed URLs)
         │
         ▼
┌─────────────────────────────┐
│          AWS                │
│  S3 + CloudFront CDN        │
│  (Media Storage + CDN)      │
└─────────────────────────────┘
```

- **AWS S3 + CloudFront**: S3 stores HLS video segments; CloudFront provides low-latency CDN delivery with signed URL security
- **PostgreSQL**: Movie metadata storage
- **Spring Boot**: RESTful API with CloudFront URL signing service

---

## Features

- **Secure Video Delivery** — CloudFront signed URL generation with configurable TTL
- **Movie Catalog API** — RESTful endpoints for browsing and retrieving movie metadata
- **Data Seeding** — Automatic database population with classic public domain films
- **CORS Configuration** — Support for multiple frontend deployments

---

## Requirements

- Java 25
- PostgreSQL (local dev)
- AWS CloudFront private key

## Getting Started

### Configuration

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

### Running

#### Using Maven wrapper

```bash
# Load env vars and run
set -a && source dev.env && set +a
./mvnw spring-boot:run
```

#### Using IDE

Configure your IDE to load environment variables from `dev.env`, then run `VaikiBackendApplication`.

---

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
    "slug": "all-quiet-on-the-western-front-1930",
    "title": "All Quiet on the Western Front",
    "year": 1930,
    "description": "...",
    "durationMinutes": 152,
    "posterUrl": "...",
    "backdropUrl": "...",
    "categories": ["War", "Drama"],
    "director": "Lewis Milestone",
    "country": "USA"
  }
]
```

**GET /api/movies/all-quiet-on-the-western-front-1930/playback-url**
```json
{
  "url": "https://d123abc.cloudfront.net/all-quiet-on-the-western-front-1930/master.m3u8?Expires=...&Signature=...&Key-Pair-Id=...",
  "expiresAt": "2026-01-15T14:30:00Z"
}
```

## Data Seeding

To seed sample movie data, include `seed` in your active profiles:

```
SPRING_PROFILES_ACTIVE=dev,seed
```

The seeder runs once on startup and only inserts data if the movies table is empty.

---

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

---

## Related

- **Frontend**: [vaiki-frontend](https://github.com/hallapmark/vaiki-frontend) — React application with HLS video player
- **Live Demo**: **[https://main.d1kv8xolprj9bu.amplifyapp.com](https://main.d1kv8xolprj9bu.amplifyapp.com)**

---

## Content Note

All films featured in this demo are in the public domain, including classics such as _All Quiet on the Western Front_ (1930), _His Girl Friday_ (1940), and _Charade_ (1963).

