package com.interpark_clone.global.response;

public record PageInfo(
        int page,
        int size,
        boolean hasNext,
        long totalElements,
        int totalPages
) {}