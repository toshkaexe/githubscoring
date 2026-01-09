package com.github.scoring.model;

import java.time.Instant;

public record GithubModel(
    String fullName,
    int stars,
    int forks,
    Instant pushedAt
) {}