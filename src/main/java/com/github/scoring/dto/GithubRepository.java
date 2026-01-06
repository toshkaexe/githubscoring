package com.github.scoring.dto;

import java.time.Instant;

public record GithubRepository(
    String fullName,
    int stars,
    int forks,
    Instant pushedAt
) {}