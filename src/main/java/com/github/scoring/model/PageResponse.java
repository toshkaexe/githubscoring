package com.github.scoring.model;

import java.util.List;

public record PageResponse<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages,
    boolean hasNext
) {
    public PageResponse(List<T> content, int page, int size, long totalElements) {
        this(
            content,
            page,
            size,
            totalElements,
            calculateTotalPages(size, totalElements),
            calculateHasNext(page, size, totalElements)
        );
    }

    private static int calculateTotalPages(int size, long totalElements) {
        if (size <= 0) return 0;
        return (int) Math.ceil((double) totalElements / size);
    }

    private static boolean calculateHasNext(int page, int size, long totalElements) {
        if (size <= 0) return false;
        return (long) page * size < totalElements;
    }
}
