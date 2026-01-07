package com.github.scoring.dto;

import java.util.List;

public record GithubSearchResponse(
    long totalCount,
    List<GithubRepository> items
) {}
