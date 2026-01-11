# GitHub Repository Scoring API

A Spring Boot REST API service for searching and scoring GitHub repositories by popularity score. 
Factors contributing to the score include stars, forks, and the recency of updates.

## Project Structure

```
src/
├── main/
│   ├── java/com/github/scoring/
│   │   ├── client/          # GitHub API client
│   │   ├── controller/      # REST controllers
│   │   ├── exception/       # Custom exceptions
│   │   ├── model/           # Data Transfer Objects and domain models
│   │   ├── service/         # Business logic and scoring algorithms
│   │   └── GithubRepositoryScoringApplication.java  # Main application class
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/github/scoring/
        ├── controller/      # Controller tests
        └── service/         # Service tests
```

## How to Run the Project

### 1. Prerequisites

- Java 21
- Maven 

### 2. Build the Project

```bash
./mvnw clean install
```

### 3. Run the Application

```bash
./mvnw spring-boot:run
```

By default, the application runs on port **8080**.

### 4. Verify the Application is Running

You can also check if the application is running by accessing the health endpoint:
```
GET http://localhost:8080/health
```

Expected response:
```
This app is working
```

## API Endpoints Overview

| Method | Endpoint | Description |
|------|---------|-------------|
| GET | `/repositories/score` | Search GitHub repositories and calculate popularity score |
| GET | `/actuator/health` | Service health check |


## 1. Search & Score Repositories

### Endpoint

GET /repositories/score

### Description

Searches GitHub repositories using the GitHub Search API and returns repositories with a calculated **popularity score**.

The score is based on:
- Number of stars
- Number of forks
- Recency of last update

### Query Parameters

| Name | Type | Required | Default | Description |
|----|------|----------|---------|-------------|
| `name` | String | ✅ Yes | — | Search keyword |
| `createdAt` | Date (yyyy-MM-dd) | ❌ No | — | Minimum repository creation date |
| `sort` | String | ❌ No | `stars` | Sort field (`stars`, `forks`, `updated`) |
| `order` | String | ❌ No | `desc` | Sort order (`asc`, `desc`) |
| `page` | Integer | ❌ No | `1` | Page number |
| `size` | Integer | ❌ No | `30` | Page size |

#### 1.  Example Request 
```
GET http://localhost:8080/api/repositories/score?name=tetris&createAt=2024-01-01&sort=stars&order=desc
```

#### 2. Basic Search (name only)

```
GET http://localhost:8080/api/repositories/score?name=tetris
```

#### 4. Search with Creation Date Filter

**URL:**
```
http://localhost:8080/api/repositories/score?name=react&createAt=2023-01-01
```

**Description:** Searches for React repositories created after January 1, 2023.

#### 5. Search with Star Sorting

**URL:**
```
http://localhost:8080/api/repositories/score?name=tetris&sort=stars&order=desc
```

#### 6. Search with Forks Sorting

**URL:**
```
http://localhost:8080/api/repositories/score?name=tensorflow&sort=forks&order=desc
```

#### 7. Search with Update Date Sorting

**URL:**
```
http://localhost:8080/api/repositories/score?name=tetris&sort=updated&order=desc
```

**Description:** Searches for tetris repositories sorted by last update date. The `updated` sort parameter orders results by the most recently updated repositories first (when using `order=desc`), helping you find actively maintained projects.

#### 8. Search with Pagination

**URL:**
```
http://localhost:8080/api/repositories/score?name=tetris&page=2&size=50
```

**Pagination parameters:**
- `page` - page number
- `size` - number of results per page 

#### 8. Full Request with All Parameters

**URL:**
```
http://localhost:8080/api/repositories/score?name=tetris&createAt=2015-01-01&sort=stars&order=desc&page=1&size=30
```
## HTTP Response Status Codes

The API returns the following HTTP status codes according to GitHub API specification:

| Status Code | Description | Response Body |
|-------------|-------------|---------------|
| **200** | OK - Request successful | JSON with repository data |
| **304** | Not Modified - Cached response is still valid | Empty (handled by caching) |
| **422** | Validation Failed - Invalid parameters or endpoint has been spammed | Error JSON with details |
| **503** | Service Unavailable - GitHub API is temporarily unavailable | Error JSON with details |

### Error Response Format

When an error with status code 422 for the case query param name is empty
```
http://localhost:8080/api/repositories/score?name=&sort=updated&order=desc&createAt=2024-01-04
```
```json
{
  "error": "Validation Failed",
  "status": 422,
  "timestamp": "2026-01-11T10:26:56.637923Z",
  "message": "Validation failed or endpoint has been spammed: Query param name cannot be empty"
}
```
## Scoring Algorithm

### Recency Score
Exponential decay based on days since last push:
```
recencyScore = e^(-days/180)
```
- Recent repositories (< 180 days) have scores close to 1.0
- Older repositories decay exponentially

### Popularity Score
Weighted logarithmic combination:
```
popularityScore = 0.5 × log(stars + 1) + 0.3 × log(forks + 1) + 0.2 × recencyScore
```
- Stars: 50%
- Forks: 30%
- Recency: 20%
- Logarithmic scaling prevents domination by mega-popular repositories

## Running Tests

```bash
./mvnw test
```


