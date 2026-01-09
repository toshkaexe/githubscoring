package com.github.scoring.service;

import com.github.scoring.model.GithubModel;
import com.github.scoring.model.ScoredModel;
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

        GithubModel recentRepo = new GithubModel(
                "owner/recent-repo",
                100,
                50,
                recent
        );

        GithubModel oldRepo = new GithubModel(
                "owner/old-repo",
                100,
                50,
                old
        );

        ScoredModel recentScored = service.score(recentRepo);
        ScoredModel oldScored = service.score(oldRepo);

        assertThat(recentScored.popularityScore())
                .isGreaterThan(oldScored.popularityScore());
    }

    @Test
    void scoreCalculatesCorrectValues() {
        Instant pushedAt = Instant.now().minus(30, ChronoUnit.DAYS);
        GithubModel repo = new GithubModel(
                "owner/test-repo",
                100,
                50,
                pushedAt
        );
        ScoredModel scored = service.score(repo);

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

        GithubModel repoWithMoreStars = new GithubModel(
                "owner/popular-repo",
                1000,
                50,
                pushedAt
        );

        GithubModel repoWithFewerStars = new GithubModel(
                "owner/less-popular-repo",
                100,
                50,
                pushedAt
        );

        ScoredModel moreStarsScored = service.score(repoWithMoreStars);
        ScoredModel fewerStarsScored = service.score(repoWithFewerStars);

        assertThat(moreStarsScored.popularityScore())
                .isGreaterThan(fewerStarsScored.popularityScore());
    }
}