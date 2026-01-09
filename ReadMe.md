# GitHub Repository Scoring API

A Spring Boot REST API service for searching and scoring GitHub repositories by popularity and recency.

## Technologies

- **Java 21**
- **Spring Boot 4.0.1**
- **Spring WebFlux** (for reactive HTTP client)
- **Maven**

## How to Run the Project

### 1. Prerequisites

- Java 21 or higher
- Maven (using embedded Maven Wrapper)

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

After startup, the API will be available at:
```
http://localhost:8080/api/repositories/score
```

You can also check if the application is running by accessing the health endpoint:
```
GET http://localhost:8080/health
```

Expected response:
```
This app is working
```

## API Endpoints

The application provides the following endpoints:

### Health Check
- **URL:** `GET /health`
- **Description:** Simple health check endpoint to verify the application is running
- **Response:** Plain text message "This app is working"
- **Example:**
  ```bash
  curl http://localhost:8080/health
  ```

### Repository Search and Scoring
- **URL:** `GET /api/repositories/score`
- **Description:** Search GitHub repositories and score them by popularity and recency
- **Parameters:** See [API Parameters](#api-parameters) section below
- **Response:** JSON with paginated scored repositories

## Testing in Postman

### Import Collection

1. Open Postman
2. Create a new Request
3. Use the **GET** method
4. Copy one of the example URLs below

### Postman Request Examples

#### 1. Basic Search (query only)

**URL:**
```
http://localhost:8080/api/repositories/score?query=spring
```

**Headers:**
```
Accept: application/json
```

**Description:** Searches for repositories with the word "spring". Uses default parameters:
- `page=1`
- `size=30` (default)

---

#### 2. Search with Language Filter

**URL:**
```
http://localhost:8080/api/repositories/score?query=tetris&language=Assembly
```

**Description:** Searches for Tetris repositories written in Assembly.

---

#### 3. Search with Creation Date Filter

**URL:**
```
http://localhost:8080/api/repositories/score?query=react&createdAfter=2023-01-01
```

**Description:** Searches for React repositories created after January 1, 2023.

---

#### 4. Search with Star Sorting

**URL:**
```
http://localhost:8080/api/repositories/score?query=kubernetes&sort=stars&order=desc
```

**Sorting parameters:**
- `sort` can be: `stars`, `forks`, `help-wanted-issues`, `updated`
- `order` can be: `desc` (descending), `asc` (ascending)

---

#### 5. Search with Forks Sorting

**URL:**
```
http://localhost:8080/api/repositories/score?query=tensorflow&sort=forks&order=desc
```

---

#### 6. Search with Update Date Sorting

**URL:**
```
http://localhost:8080/api/repositories/score?query=vue&sort=updated&order=desc
```

**Description:** Searches for Vue repositories sorted by last update date. The `updated` sort parameter orders results by the most recently updated repositories first (when using `order=desc`), helping you find actively maintained projects.

---

#### 7. Search with Pagination

**URL:**
```
http://localhost:8080/api/repositories/score?query=django&page=2&size=50
```

**Pagination parameters:**
- `page` - page number (starts at 1, minimum: 1)
- `size` - number of results per page (default: 30, maximum: 100)

---

#### 8. Full Request with All Parameters

**URL:**
```
http://localhost:8080/api/repositories/score?query=tetris&language=Assembly&createdAfter=2015-01-01&sort=stars&order=desc&page=1&size=30
```

**Description:** Complex query using all available parameters.

---

### Response Example

```json
{
  "content": [
    {
      "fullName": "daniel-e/tetros",
      "stars": 785,
      "forks": 39,
      "lastPush": "2016-12-18T13:32:27Z",
      "recencyScore": 1.055499460617958E-8,
      "popularityScore": 4.440142234559782
    },
    {
      "fullName": "kirjavascript/TetrisGYM",
      "stars": 244,
      "forks": 21,
      "lastPush": "2025-12-23T00:09:33Z",
      "recencyScore": 0.9200444146293233,
      "popularityScore": 3.861950724205723
    }
  ],
  "page": 1,
  "size": 30,
  "totalElements": 4194,
  "totalPages": 140,
  "hasNext": true
}
```

### Response Fields Description

- `fullName` - full repository name (owner/repo)
- `stars` - number of stars
- `forks` - number of forks
- `lastPush` - last push date (ISO 8601)
- `recencyScore` - recency score (0-1, where 1 = very recent)
- `popularityScore` - overall popularity score (combination of stars, forks, recency)
- `page` - current page
- `size` - page size
- `totalElements` - total number of repositories found
- `totalPages` - total number of pages
- `hasNext` - whether there is a next page

## API Parameters

### Required Parameters

| Parameter | Type | Description |
|----------|-----|----------|
| `query` | String | Search query (required) |

### Optional Parameters

| Parameter | Type | Default Value | Description |
|----------|-----|----------------------|----------|
| `language` | String | - | Filter by programming language |
| `createdAfter` | LocalDate (YYYY-MM-DD) | - | Filter by creation date |
| `sort` | String | - | Sort by: `stars`, `forks`, `help-wanted-issues`, `updated` |
| `order` | String | `desc` | Order: `desc` (descending), `asc` (ascending) |
| `page` | Integer | 1 | Page number (minimum: 1) |
| `size` | Integer | 30 | Page size (minimum: 1, maximum: 100) |

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

## Project Structure

```
src/
├── main/
│   ├── java/com/github/scoring/
│   │   ├── client/          # GitHub API client
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   └── service/        # Business logic and scoring algorithms
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/github/scoring/
```

## Configuration

### application.properties

```properties
# GitHub API base URL (optional)
github.api.url=https://api.github.com

# GitHub API Accept header (optional)
github.api.accept=application/vnd.github+json
```

## Frequently Asked Questions

**Q: How many results can I get in one request?**
A: Maximum 100 repositories per request (`size` parameter). Default is 30.

**Q: Can I combine filters?**
A: Yes, all filters (`language`, `createdAfter`, `sort`, `order`) can be combined.

**Q: What does a `recencyScore` close to 0 mean?**
A: It means the repository hasn't been updated in a very long time (several years).

**Q: How does the `sort` parameter work?**
A: The `sort` parameter is passed to the GitHub API and affects the order of results BEFORE calculating `popularityScore`. After receiving results, they are additionally sorted by `popularityScore`.