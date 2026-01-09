package com.github.scoring.exception;

/**
 * Exception thrown when GitHub API returns 422 (Validation failed, or the endpoint has been spammed)
 */
public class GithubValidationException extends GithubApiException {
    public GithubValidationException(String message) {
        super(message, 422);
    }

    public GithubValidationException(String message, Throwable cause) {
        super(message, 422, cause);
    }
}