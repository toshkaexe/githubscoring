package com.github.scoring.model;

import java.util.List;

public record GithubSearchResponse(
    long totalCount,
    List<GithubModel> items
) {}
