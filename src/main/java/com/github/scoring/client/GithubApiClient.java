package com.github.scoring.client;

import com.github.scoring.dto.GithubRepository;
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

    private final WebClient webClient;

    public GithubApiClient(@Value("${github.api.url:https://api.github.com}") String githubApiUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(githubApiUrl)
                .defaultHeader("Accept", "application/vnd.github.v3+json")
                .build();
    }

    public List<GithubRepository> search(String query, String language, LocalDate createdAfter) {
        StringBuilder searchQuery = new StringBuilder(query);

        if (language != null && !language.isBlank()) {
            searchQuery.append(" language:").append(language);
        }

        if (createdAfter != null) {
            searchQuery.append(" created:>=").append(createdAfter.format(DateTimeFormatter.ISO_DATE));
        }

        Map<String, Object> response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search/repositories")
                        .queryParam("q", searchQuery.toString())
                        .queryParam("sort", "stars")
                        .queryParam("order", "desc")
                        .queryParam("per_page", 30)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || !response.containsKey("items")) {
            return List.of();
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

        return items.stream()
                .map(this::mapToGithubRepository)
                .toList();
    }

    private GithubRepository mapToGithubRepository(Map<String, Object> item) {
        String fullName = (String) item.get("full_name");
        Integer stars = (Integer) item.get("stargazers_count");
        Integer forks = (Integer) item.get("forks_count");
        String pushedAtStr = (String) item.get("pushed_at");

        Instant pushedAt = pushedAtStr != null ? Instant.parse(pushedAtStr) : null;

        return new GithubRepository(
                fullName,
                stars != null ? stars : 0,
                forks != null ? forks : 0,
                pushedAt
        );
    }
}