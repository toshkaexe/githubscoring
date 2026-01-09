# GitHub Repository Scoring API

A Spring Boot REST API service for searching and scoring GitHub repositories by popularity and recency. Factors contributing to the score include stars, forks, and the recency of updates.

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

### Step-by-Step Guide to Create a Request

#### 1. Open Postman and Create a New Request

1. Launch **Postman** application
2. Click on **"New"** → **"HTTP Request"** (or press Ctrl+N / Cmd+N)
3. You'll see a new request tab

#### 2. Configure the Request Method and URL

1. **Method**: Select **GET** from the dropdown (left side)
2. **URL**: Enter the full endpoint URL:
   ```
   http://localhost:8080/api/repositories/score
   ```
3. Make sure your Spring Boot application is running on port 8080

#### 3. Add Query Parameters

**Option A: Using Params Tab (Recommended)**
1. Click on the **"Params"** tab (below the URL field)
2. Add parameters as key-value pairs:

| Key | Value | Description |
|-----|-------|-------------|
| `query` | `spring` | Required: Search term |
| `language` | `Java` | Optional: Filter by language |
| `createAt` | `2023-01-01` | Optional: Filter by creation date |
| `sort` | `stars` | Optional: Sort criteria |
| `order` | `desc` | Optional: Sort order |
| `page` | `1` | Optional: Page number (default: 1) |
| `size` | `30` | Optional: Results per page (default: 30) |

**Option B: Direct URL Entry**
Alternatively, paste the complete URL with parameters:
```
http://localhost:8080/api/repositories/score?query=spring&language=Java&sort=stars&order=desc
```

#### 4. Set Headers (Optional but Recommended)

1. Click on the **"Headers"** tab
2. Add the following header:

| Key | Value |
|-----|-------|
| `Accept` | `application/json` |

This ensures you receive JSON formatted responses.

#### 5. Send the Request

1. Click the blue **"Send"** button
2. Wait for the response (usually 1-3 seconds)
3. View results in the **"Body"** section below

#### 6. Verify the Response

**Expected Response (Status: 200 OK)**
```
{
  "content": [
    {
      "fullName": "spring-projects/spring-boot",
      "stars": 70000,
      "forks": 40000,
      "lastPush": "2026-01-08T10:30:00Z",
      "recencyScore": 0.99,
      "popularityScore": 15.5
    }...
    
  ],
  "page": 1,
  "size": 30,
  "totalElements": 1000,
  "totalPages": 34,
  "hasNext": true
}
```

#### 7. Save Your Request (Optional)

1. Click **"Save"** button (top right)
2. Name it: e.g., "GitHub Search - Spring Repos"
3. Create or select a collection to organize your requests

### Common Issues and Solutions

❌ **Error: "Connection refused"**
- ✅ Make sure your Spring Boot app is running: `./mvnw spring-boot:run`
- ✅ Check the port is correct (default: 8080)

❌ **Error: "Required request parameter 'query' is not present"**
- ✅ Add the `query` parameter in the Params tab
- ✅ The `query` parameter is mandatory

❌ **Empty results**
- ✅ Try a more common search term (e.g., "react", "spring", "java")
- ✅ Remove restrictive filters like `language` or `createAt`

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
http://localhost:8080/api/repositories/score?query=react&createAt=2023-01-01
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
http://localhost:8080/api/repositories/score?query=tetris&language=Assembly&createAt=2015-01-01&sort=stars&order=desc&page=1&size=30
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
| `createAt` | LocalDate (YYYY-MM-DD) | - | Filter by creation date |
| `sort` | String | - | Sort by: `stars`, `forks`, `help-wanted-issues`, `updated` |
| `order` | String | `desc` | Order: `desc` (descending), `asc` (ascending) |
| `page` | Integer | 1 | Page number (minimum: 1) |
| `size` | Integer | 30 | Page size (minimum: 1, maximum: 100) |

## HTTP Response Status Codes

The API returns the following HTTP status codes according to GitHub API specification:

| Status Code | Description | Response Body |
|-------------|-------------|---------------|
| **200** | OK - Request successful | JSON with repository data |
| **304** | Not Modified - Cached response is still valid | Empty (handled by caching) |
| **422** | Validation Failed - Invalid parameters or endpoint has been spammed | Error JSON with details |
| **503** | Service Unavailable - GitHub API is temporarily unavailable | Error JSON with details |

### Error Response Format

When an error occurs (422 or 503), the API returns a JSON response with the following structure:

```json
{
  "timestamp": "2026-01-09T12:30:45.123Z",
  "status": 422,
  "error": "Validation Failed",
  "message": "Validation failed or endpoint has been spammed: <details from GitHub API>"
}
```

### Examples of Error Responses

**422 Validation Failed:**
```json
{
  "timestamp": "2026-01-09T12:30:45.123Z",
  "status": 422,
  "error": "Validation Failed",
  "message": "Validation failed or endpoint has been spammed: Only the first 1000 search results are available"
}
```

**503 Service Unavailable:**
```json
{
  "timestamp": "2026-01-09T12:30:45.123Z",
  "status": 503,
  "error": "Service Unavailable",
  "message": "GitHub API service unavailable: API rate limit exceeded"
}
```

### Handling Errors in Postman

When testing in Postman:
- ✅ Check the **Status** code in the response (top right)
- ✅ Read the error **message** field for details
- ✅ For **422** errors: Try reducing the `page` number or simplifying your query
- ✅ For **503** errors: Wait a few moments and retry - GitHub API may be experiencing issues

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
A: Yes, all filters (`language`, `createAt`, `sort`, `order`) can be combined.

**Q: What does a `recencyScore` close to 0 mean?**
A: It means the repository hasn't been updated in a very long time (several years).

**Q: How does the `sort` parameter work?**
A: When you specify the `sort` parameter (e.g., `sort=forks`, `sort=stars`, `sort=updated`), results are sorted by GitHub API according to your choice and the `popularityScore` is calculated but NOT used for sorting. If you DON'T specify `sort`, results are automatically sorted by `popularityScore` (our custom algorithm combining stars, forks, and recency).