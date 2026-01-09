package com.github.scoring.service;

import com.github.scoring.client.GithubApiClient;
import com.github.scoring.model.GithubSearchResponse;
import com.github.scoring.model.PageResponse;
import com.github.scoring.model.ScoredModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

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
        // Fetch from GitHub with pagination
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
}