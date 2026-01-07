package com.github.scoring.model;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

public class PaginationRequest {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_PAGE_SIZE = 30;
    public static final int MAX_PAGE_SIZE = 100;

    @Min(value = 1, message = "page must be greater than or equal to 1")
    private final int page;

    @Min(value = 1, message = "size must be greater than or equal to 1")
    @Max(value = MAX_PAGE_SIZE, message = "size must not exceed 100")
    private final int size;

    public PaginationRequest() {
        this.page = DEFAULT_PAGE;
        this.size = DEFAULT_PAGE_SIZE;
    }

    public PaginationRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
}
