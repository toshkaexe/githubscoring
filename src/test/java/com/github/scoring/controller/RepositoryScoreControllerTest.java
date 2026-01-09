package com.github.scoring.controller;

import com.github.scoring.exception.GithubServiceUnavailableException;
import com.github.scoring.exception.GithubValidationException;
import com.github.scoring.model.PageResponse;
import com.github.scoring.model.ScoredModel;
import com.github.scoring.service.SearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for RepositoryScoreController
 */
@ExtendWith(MockitoExtension.class)
class RepositoryScoreControllerTest {

    @Mock
    private SearchService searchService;

    @InjectMocks
    private RepositoryScoreController controller;

    private PageResponse<ScoredModel> mockResponse;

    @BeforeEach
    void setUp() {
        ScoredModel repo1 = new ScoredModel(
                "daniel-e/tetros",
                785,
                39,
                Instant.parse("2016-12-18T13:32:27Z"),
                1.055499460617958E-8,
                4.440142234559782
        );

        ScoredModel repo2 = new ScoredModel(
                "kirjavascript/TetrisGYM",
                244,
                21,
                Instant.parse("2025-12-23T00:09:33Z"),
                0.9200444146293233,
                3.861950724205723
        );

        mockResponse = new PageResponse<>(
                List.of(repo1, repo2),
                1,
                30,
                4194L
        );
    }

    @Test
    void testScenario1_BasicSearchWithNameOnly() {
        // Scenario 1: Basic Search (name only)
        // GET /api/repositories/score?name=tetris
        when(searchService.searchAndScore(
                eq("tetris"),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(1),
                eq(30)
        )).thenReturn(mockResponse);

        PageResponse<ScoredModel> response = controller.scoreRepositories(
                "tetris", null, null, null, null, 1, 30
        );

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);
        assertThat(response.content().getFirst().fullName()).isEqualTo("daniel-e/tetros");
        assertThat(response.content().getFirst().stars()).isEqualTo(785);
        assertThat(response.page()).isEqualTo(1);
        assertThat(response.size()).isEqualTo(30);
        assertThat(response.totalElements()).isEqualTo(4194);
        assertThat(response.totalPages()).isEqualTo(140);
        assertThat(response.hasNext()).isTrue();

        verify(searchService).searchAndScore("tetris", null, null, null, null, 1, 30);
    }

    @Test
    void testScenario2_SearchWithLanguageFilter() {
        // Scenario 2: Search with Language Filter
        // GET /api/repositories/score?name=tetris&language=Assembly
        when(searchService.searchAndScore(
                eq("tetris"),
                eq("Assembly"),
                isNull(),
                isNull(),
                isNull(),
                eq(1),
                eq(30)
        )).thenReturn(mockResponse);

        PageResponse<ScoredModel> response = controller.scoreRepositories(
                "tetris", "Assembly", null, null, null, 1, 30
        );

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);
        assertThat(response.content().getFirst().fullName()).isEqualTo("daniel-e/tetros");

        verify(searchService).searchAndScore("tetris", "Assembly", null, null, null, 1, 30);
    }

    @Test
    void testScenario3_SearchWithCreationDateFilter() {
        // Scenario 3: Search with Creation Date Filter
        // GET /api/repositories/score?name=react&createAt=2023-01-01
        LocalDate createAt = LocalDate.parse("2023-01-01");

        when(searchService.searchAndScore(
                eq("react"),
                isNull(),
                eq(createAt),
                isNull(),
                isNull(),
                eq(1),
                eq(30)
        )).thenReturn(mockResponse);

        PageResponse<ScoredModel> response = controller.scoreRepositories(
                "react", null, createAt, null, null, 1, 30
        );

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);

        verify(searchService).searchAndScore("react", null, createAt, null, null, 1, 30);
    }

    @Test
    void testScenario4_SearchWithStarSorting() {
        // Scenario 4: Search with Star Sorting
        // GET /api/repositories/score?name=kubernetes&sort=stars&order=desc
        when(searchService.searchAndScore(
                eq("kubernetes"),
                isNull(),
                isNull(),
                eq("stars"),
                eq("desc"),
                eq(1),
                eq(30)
        )).thenReturn(mockResponse);

        PageResponse<ScoredModel> response = controller.scoreRepositories(
                "kubernetes", null, null, "stars", "desc", 1, 30
        );

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);

        verify(searchService).searchAndScore("kubernetes", null, null, "stars", "desc", 1, 30);
    }

    @Test
    void testScenario5_SearchWithForksSorting() {
        // Scenario 5: Search with Forks Sorting
        // GET /api/repositories/score?name=tensorflow&sort=forks&order=desc
        when(searchService.searchAndScore(
                eq("tensorflow"),
                isNull(),
                isNull(),
                eq("forks"),
                eq("desc"),
                eq(1),
                eq(30)
        )).thenReturn(mockResponse);

        PageResponse<ScoredModel> response = controller.scoreRepositories(
                "tensorflow", null, null, "forks", "desc", 1, 30
        );

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);

        verify(searchService).searchAndScore("tensorflow", null, null, "forks", "desc", 1, 30);
    }

    @Test
    void testScenario6_SearchWithUpdateDateSorting() {
        // Scenario 6: Search with Update Date Sorting
        // GET /api/repositories/score?name=vue&sort=updated&order=desc
        when(searchService.searchAndScore(
                eq("vue"),
                isNull(),
                isNull(),
                eq("updated"),
                eq("desc"),
                eq(1),
                eq(30)
        )).thenReturn(mockResponse);

        PageResponse<ScoredModel> response = controller.scoreRepositories(
                "vue", null, null, "updated", "desc", 1, 30
        );

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(2);

        verify(searchService).searchAndScore("vue", null, null, "updated", "desc", 1, 30);
    }

    @Test
    void testScenario7_SearchWithPagination() {
        // Scenario 7: Search with Pagination
        // GET /api/repositories/score?name=django&page=2&size=50
        ScoredModel repo = new ScoredModel(
                "django/django",
                1000,
                500,
                Instant.now(),
                0.95,
                10.5
        );
        PageResponse<ScoredModel> paginatedResponse = new PageResponse<>(
                List.of(repo),
                2,
                50,
                1000L
        );

        when(searchService.searchAndScore(
                eq("django"),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(2),
                eq(50)
        )).thenReturn(paginatedResponse);

        PageResponse<ScoredModel> response = controller.scoreRepositories(
                "django", null, null, null, null, 2, 50
        );

        assertThat(response).isNotNull();
        assertThat(response.page()).isEqualTo(2);
        assertThat(response.size()).isEqualTo(50);
        assertThat(response.totalElements()).isEqualTo(1000);
        assertThat(response.totalPages()).isEqualTo(20);
        assertThat(response.hasNext()).isTrue();

        verify(searchService).searchAndScore("django", null, null, null, null, 2, 50);
    }


    @Test
    void testScenario8_GithubValidationException_InvalidQuery() {
        // Test GitHub API 422 error: Invalid query parameters
        String errorMessage = "Validation failed or endpoint has been spammed: Invalid query";

        when(searchService.searchAndScore(
                eq(""),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(1),
                eq(30)
        )).thenThrow(new GithubValidationException(errorMessage));

        assertThatThrownBy(() -> controller.scoreRepositories(
                "", null, null, null, null, 1, 30
        ))
                .isInstanceOf(GithubValidationException.class)
                .hasMessage(errorMessage);
    }

    @Test
    void testScenario9_GithubServiceUnavailableException_ServiceDown() {
        // Test GitHub API 503 error: Service temporarily unavailable
        String errorMessage = "GitHub API service unavailable: Service temporarily down for maintenance";

        when(searchService.searchAndScore(
                eq("vue"),
                eq("JavaScript"),
                isNull(),
                eq("stars"),
                eq("desc"),
                eq(1),
                eq(30)
        )).thenThrow(new GithubServiceUnavailableException(errorMessage));

        assertThatThrownBy(() -> controller.scoreRepositories(
                "vue", "JavaScript", null, "stars", "desc", 1, 30
        ))
                .isInstanceOf(GithubServiceUnavailableException.class)
                .hasMessage(errorMessage);

        verify(searchService).searchAndScore("vue", "JavaScript", null, "stars", "desc", 1, 30);
    }

    @Test
    void testScenario10_ValidationException_InvalidQueryParameter() {
        // Test GitHub API 422 error: Invalid query parameter
        String errorMessage = "Validation failed or endpoint has been spammed: Invalid search query";

        when(searchService.searchAndScore(
                eq("@#$%^&*"),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(1),
                eq(30)
        )).thenThrow(new GithubValidationException(errorMessage));

        assertThatThrownBy(() -> controller.scoreRepositories(
                "@#$%^&*", null, null, null, null, 1, 30
        ))
                .isInstanceOf(GithubValidationException.class)
                .hasMessageContaining("Invalid search query");

        verify(searchService).searchAndScore("@#$%^&*", null, null, null, null, 1, 30);
    }

}
