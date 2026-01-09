package com.github.scoring.controller;

import com.github.scoring.dto.PageResponse;
import com.github.scoring.dto.ScoredRepositoryDto;
import com.github.scoring.service.GithubSearchService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/repositories")
@Validated
public class RepositoryScoreController {

    private final GithubSearchService githubSearchService;

    public RepositoryScoreController(GithubSearchService githubSearchService) {
        this.githubSearchService = githubSearchService;
    }

    @GetMapping("/score")
    public PageResponse<ScoredRepositoryDto> scoreRepositories(
            @RequestParam String query,
            @RequestParam(required = false) String language,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate createAt,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String order,
            @RequestParam(defaultValue = "1")
            @Min(value = 1, message = "page must be greater than or equal to 1")
            int page,
            @RequestParam(defaultValue = "30")
            @Min(value = 1, message = "size must be greater than or equal to 1")
            @Max(value = 100, message = "size must not exceed 100")
            int size
    ) {
        return githubSearchService.searchAndScore(query, language, createAt, sort, order, page, size);
    }
}