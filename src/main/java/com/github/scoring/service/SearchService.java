package com.github.scoring.service;

import com.github.scoring.client.GithubApiClient;
import com.github.scoring.exception.GithubValidationException;
import com.github.scoring.model.GithubSearchResponse;
import com.github.scoring.model.PageResponse;
import com.github.scoring.model.ScoredModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class SearchService {

    // Pattern to validate search query - allows alphanumeric, spaces, hyphens, underscores, dots, and GitHub search operators
    private static final Pattern INVALID_QUERY_PATTERN = Pattern.compile("[<>@#$%^&*(){}\\[\\]|\\\\]");

    private final GithubApiClient githubApiClient;
    private final PopularityScoreService scoreService;

    public PageResponse<ScoredModel> searchAndScore(
            String name,
            String language,
            LocalDate createAt,
            String sort,
            String order,
            int page,
            int size
    ) {
        validateName(name);

        GithubSearchResponse searchResponse = githubApiClient.search(
                name,
                language,
                createAt,
                sort,
                order,
                page,
                size
        );

        // Score the items from this page
        var stream = searchResponse.items()
                .stream()
                .map(scoreService::score);

        // Only sort by popularityScore if user didn't specify sort parameter
        if (sort == null || sort.isBlank()) {
            stream = stream.sorted(Comparator.comparingDouble(
                    ScoredModel::popularityScore).reversed());
        }

        List<ScoredModel> scoredRepositories = stream.toList();

        // Build paginated response
        return new PageResponse<>(
                scoredRepositories,
                page,
                size,
                searchResponse.totalCount()
        );
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new GithubValidationException("Validation failed or endpoint has been spammed: Query cannot be empty");
        }

        if (INVALID_QUERY_PATTERN.matcher(name).find()) {
            throw new GithubValidationException("Validation failed or endpoint has been spammed: Query contains invalid characters");
        }

        if (name.length() > 256) {
            throw new GithubValidationException("Validation failed or endpoint has been spammed: Query is too long");
        }
    }
}