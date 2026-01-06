package com.github.scoring.service;

import com.github.scoring.dto.GithubRepository;
import com.github.scoring.dto.ScoredRepositoryDto;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;

class PopularityScoreServiceTest {

    private final PopularityScoreService service = new PopularityScoreService();

    @Test
    void newerRepositoryHasHigherRecencyScore() {
        Instant recent = Instant.now().minus(10, ChronoUnit.DAYS);
        Instant old = Instant.now().minus(300, ChronoUnit.DAYS);

        assertThat(service.calculateRecency(recent))
                .isGreaterThan(service.calculateRecency(old));
    }

    @Test
    void nullPushedAtReturnsZeroRecencyScore() {
        assertThat(service.calculateRecency(null)).isEqualTo(0.0);
    }

    @Test
    void repositoryWithSameStarsAndForksButNewerPushHasHigherPopularityScore() {
        Instant recent = Instant.now().minus(10, ChronoUnit.DAYS);
        Instant old = Instant.now().minus(300, ChronoUnit.DAYS);

        GithubRepository recentRepo = new GithubRepository(
                "owner/recent-repo",
                100,
                50,
                recent
        );

        GithubRepository oldRepo = new GithubRepository(
                "owner/old-repo",
                100,
                50,
                old
        );

        ScoredRepositoryDto recentScored = service.score(recentRepo);
        ScoredRepositoryDto oldScored = service.score(oldRepo);

        assertThat(recentScored.popularityScore())
                .isGreaterThan(oldScored.popularityScore());
    }

    @Test
    void scoreCalculatesCorrectValues() {
        Instant pushedAt = Instant.now().minus(30, ChronoUnit.DAYS);
        GithubRepository repo = new GithubRepository(
                "owner/test-repo",
                100,
                50,
                pushedAt
        );

        ScoredRepositoryDto scored = service.score(repo);

        assertThat(scored.fullName()).isEqualTo("owner/test-repo");
        assertThat(scored.stars()).isEqualTo(100);
        assertThat(scored.forks()).isEqualTo(50);
        assertThat(scored.lastPush()).isEqualTo(pushedAt);
        assertThat(scored.recencyScore()).isGreaterThan(0.0);
        assertThat(scored.popularityScore()).isGreaterThan(0.0);
    }

    @Test
    void repositoryWithMoreStarsHasHigherPopularityScore() {
        Instant pushedAt = Instant.now().minus(30, ChronoUnit.DAYS);

        GithubRepository repoWithMoreStars = new GithubRepository(
                "owner/popular-repo",
                1000,
                50,
                pushedAt
        );

        GithubRepository repoWithFewerStars = new GithubRepository(
                "owner/less-popular-repo",
                100,
                50,
                pushedAt
        );

        ScoredRepositoryDto moreStarsScored = service.score(repoWithMoreStars);
        ScoredRepositoryDto fewerStarsScored = service.score(repoWithFewerStars);

        assertThat(moreStarsScored.popularityScore())
                .isGreaterThan(fewerStarsScored.popularityScore());
    }
}