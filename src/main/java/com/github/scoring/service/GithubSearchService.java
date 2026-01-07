package com.github.scoring.service;

import com.github.scoring.client.GithubApiClient;
import com.github.scoring.dto.GithubSearchResponse;
import com.github.scoring.dto.PageResponse;
import com.github.scoring.dto.ScoredRepositoryDto;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
public class GithubSearchService {

    private final GithubApiClient githubApiClient;
    private final PopularityScoreService scoreService;

    public GithubSearchService(
            GithubApiClient githubApiClient,
            PopularityScoreService scoreService
    ) {
        this.githubApiClient = githubApiClient;
        this.scoreService = scoreService;
    }

    public PageResponse<ScoredRepositoryDto> searchAndScore(
            String query,
            String language,
            LocalDate createdAfter,
            int page,
            int size
    ) {
        // Fetch from GitHub with pagination
        GithubSearchResponse searchResponse = githubApiClient.search(
                query,
                language,
                createdAfter,
                page,
                size
        );

        // Score and sort the items from this page
        List<ScoredRepositoryDto> scoredRepositories = searchResponse.items()
                .stream()
                .map(scoreService::score)
                .sorted(Comparator.comparingDouble(
                        ScoredRepositoryDto::popularityScore).reversed())
                .toList();

        // Build paginated response
        return new PageResponse<>(
                scoredRepositories,
                page,
                size,
                searchResponse.totalCount()
        );
    }
}