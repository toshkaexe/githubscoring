package com.github.scoring.service;

import com.github.scoring.client.GithubApiClient;
import com.github.scoring.dto.GithubSearchResponse;
import com.github.scoring.dto.PageResponse;
import com.github.scoring.dto.ScoredRepositoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GithubSearchService {

    private final GithubApiClient githubApiClient;
    private final PopularityScoreService scoreService;

    public PageResponse<ScoredRepositoryDto> searchAndScore(
            String query,
            String language,
            LocalDate createAt,
            String sort,
            String order,
            int page,
            int size
    ) {
        // Fetch from GitHub with pagination
        GithubSearchResponse searchResponse = githubApiClient.search(
                query,
                language,
                createAt,
                sort,
                order,
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