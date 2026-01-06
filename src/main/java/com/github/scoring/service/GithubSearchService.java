package com.github.scoring.service;

import com.github.scoring.client.GithubApiClient;
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

    public List<ScoredRepositoryDto> searchAndScore(
            String query,
            String language,
            LocalDate createdAfter
    ) {
        return githubApiClient.search(query, language, createdAfter)
                .stream()
                .map(scoreService::score)
                .sorted(Comparator.comparingDouble(
                        ScoredRepositoryDto::popularityScore).reversed())
                .toList();
    }
}