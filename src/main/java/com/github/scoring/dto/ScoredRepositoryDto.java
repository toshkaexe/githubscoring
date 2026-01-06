package com.github.scoring.dto;

import java.time.Instant;

public record ScoredRepositoryDto(
    String fullName,
    int stars,
    int forks,
    Instant lastPush,
    double recencyScore,
    double popularityScore
) {}