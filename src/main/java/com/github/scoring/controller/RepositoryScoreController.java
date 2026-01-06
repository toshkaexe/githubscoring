package com.github.scoring.controller;

import com.github.scoring.dto.ScoredRepositoryDto;
import com.github.scoring.service.GithubSearchService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/repositories")
public class RepositoryScoreController {

    private final GithubSearchService githubSearchService;

    public RepositoryScoreController(GithubSearchService githubSearchService) {
        this.githubSearchService = githubSearchService;
    }

    @GetMapping("/score")
    public List<ScoredRepositoryDto> scoreRepositories(
            @RequestParam String query,
            @RequestParam(required = false) String language,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createdAfter
    ) {
        return githubSearchService.searchAndScore(query, language, createdAfter);
    }
}