package com.github.scoring.model;

import java.time.Instant;

public record ScoredModel(
    String fullName,
    int stars,
    int forks,
    Instant lastPush,
    double recencyScore,
    double popularityScore
) {}