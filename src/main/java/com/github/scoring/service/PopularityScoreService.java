package com.github.scoring.service;

import com.github.scoring.model.GithubModel;
import com.github.scoring.model.ScoredModel;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PopularityScoreService {

    public ScoredModel score(GithubModel repo) {
        double recency = calculateRecency(repo.pushedAt());
        double popularity =
                0.5 * Math.log(repo.stars() + 1)
              + 0.3 * Math.log(repo.forks() + 1)
              + 0.2 * recency;

        return new ScoredModel(
                repo.fullName(),
                repo.stars(),
                repo.forks(),
                repo.pushedAt(),
                recency,
                popularity
        );
    }

    double calculateRecency(Instant pushedAt) {
        if (pushedAt == null) return 0.0;

        long days = ChronoUnit.DAYS.between(pushedAt, Instant.now());
        return Math.exp(-days / 180.0);
    }
}