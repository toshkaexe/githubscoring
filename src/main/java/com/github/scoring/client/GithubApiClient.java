package com.github.scoring.client;

import com.github.scoring.dto.GithubRepository;
import com.github.scoring.dto.GithubSearchResponse;
import com.github.scoring.exception.GithubApiException;
import com.github.scoring.exception.GithubServiceUnavailableException;
import com.github.scoring.exception.GithubValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Component
public class GithubApiClient {

    private static final String GITHUB_API_URL_PROPERTY = "${github.api.url:https://api.github.com}";
    private static final String GITHUB_API_ACCEPT_PROPERTY = "${github.api.accept:application/vnd.github+json}";
    private static final String SEARCH_REPOSITORIES_PATH = "/search/repositories";
    private static final String ACCEPT_HEADER = "Accept";

    // Query parameters
    private static final String QUERY_PARAM = "q";
    private static final String SORT_PARAM = "sort";
    private static final String ORDER_PARAM = "order";
    private static final String PAGE_PARAM = "page";
    private static final String PER_PAGE_PARAM = "per_page";

    // Response fields
    private static final String ITEMS_FIELD = "items";
    private static final String TOTAL_COUNT_FIELD = "total_count";

    // Repository fields
    private static final String FULL_NAME_FIELD = "full_name";
    private static final String STARGAZERS_COUNT_FIELD = "stargazers_count";
    private static final String FORKS_COUNT_FIELD = "forks_count";
    private static final String PUSHED_AT_FIELD = "pushed_at";

    // Search query templates
    private static final String LANGUAGE_FILTER = " language:";
    private static final String CREATED_FILTER = " created:>=";

    private final WebClient webClient;

    public GithubApiClient(
            @Value(GITHUB_API_URL_PROPERTY) String githubApiUrl,
            @Value(GITHUB_API_ACCEPT_PROPERTY) String acceptHeader
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(githubApiUrl)
                .defaultHeader(ACCEPT_HEADER, acceptHeader)
                .build();
    }

    public GithubSearchResponse search(String name, String language, LocalDate createAt, String sort, String order, int page, int size) {
        StringBuilder searchQuery = new StringBuilder(name);

        if (language != null && !language.isBlank()) {
            searchQuery.append(LANGUAGE_FILTER).append(language);
        }

        if (createAt != null) {
            searchQuery.append(CREATED_FILTER).append(createAt.format(DateTimeFormatter.ISO_DATE));
        }

        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                            .path(SEARCH_REPOSITORIES_PATH)
                            .queryParam(QUERY_PARAM, searchQuery.toString());

                    if (sort != null && !sort.isBlank()) {
                        builder.queryParam(SORT_PARAM, sort);
                    }

                    if (order != null && !order.isBlank()) {
                        builder.queryParam(ORDER_PARAM, order);
                    }

                    return builder
                            .queryParam(PAGE_PARAM, page)
                            .queryParam(PER_PAGE_PARAM, size)
                            .build();
                })
                .retrieve()
                .onStatus(
                        status -> status.value() == 304,
                        response304 -> response304.createException().map(ex ->
                                new GithubApiException("GitHub API returned 304 Not Modified", 304, ex)
                        )
                )
                .onStatus(
                        status -> status.value() == 422,
                        response422 -> response422.bodyToMono(String.class).map(body ->
                                new GithubValidationException("Validation failed or endpoint has been spammed: " + body)
                        )
                )
                .onStatus(
                        status -> status.value() == 503,
                        response503 -> response503.bodyToMono(String.class).map(body ->
                                new GithubServiceUnavailableException("GitHub API service unavailable: " + body)
                        )
                )
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey(ITEMS_FIELD)) {
            return new GithubSearchResponse(0, List.of());
        }

        // Extract total_count
        long totalCount = 0;
        if (response.containsKey(TOTAL_COUNT_FIELD)) {
            Object totalCountObj = response.get(TOTAL_COUNT_FIELD);
            if (totalCountObj instanceof Integer) {
                totalCount = ((Integer) totalCountObj).longValue();
            } else if (totalCountObj instanceof Long) {
                totalCount = (Long) totalCountObj;
            }
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get(ITEMS_FIELD);
        List<GithubRepository> repositories = items.stream()
                .map(this::mapToGithubRepository)
                .toList();

        return new GithubSearchResponse(totalCount, repositories);
    }

    private GithubRepository mapToGithubRepository(Map<String, Object> item) {
        String fullName = (String) item.get(FULL_NAME_FIELD);
        Integer stars = (Integer) item.get(STARGAZERS_COUNT_FIELD);
        Integer forks = (Integer) item.get(FORKS_COUNT_FIELD);
        String pushedAtStr = (String) item.get(PUSHED_AT_FIELD);

        Instant pushedAt = pushedAtStr != null ? Instant.parse(pushedAtStr) : null;

        return new GithubRepository(
                fullName,
                stars != null ? stars : 0,
                forks != null ? forks : 0,
                pushedAt
        );
    }
}