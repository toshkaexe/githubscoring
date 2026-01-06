package com.github.scoring.service;

import com.github.scoring.dto.GithubRepository;
import com.github.scoring.dto.ScoredRepositoryDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class PopularityScoreService {

    public ScoredRepositoryDto score(GithubRepository repo) {
        double recency = calculateRecency(repo.pushedAt());
        double popularity =
                0.5 * Math.log(repo.stars() + 1)
              + 0.3 * Math.log(repo.forks() + 1)
              + 0.2 * recency;

        return new ScoredRepositoryDto(
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