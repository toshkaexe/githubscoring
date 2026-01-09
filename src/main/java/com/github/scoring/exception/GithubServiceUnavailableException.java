package com.github.scoring.exception;

/**
 * Exception thrown when GitHub API returns 503 (Service unavailable)
 */
public class GithubServiceUnavailableException extends GithubApiException {
    public GithubServiceUnavailableException(String message) {
        super(message, 503);
    }

    public GithubServiceUnavailableException(String message, Throwable cause) {
        super(message, 503, cause);
    }
}