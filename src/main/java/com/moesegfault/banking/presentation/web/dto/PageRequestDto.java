package com.moesegfault.banking.presentation.web.dto;

import com.moesegfault.banking.presentation.web.WebRequest;
import java.util.Objects;

/**
 * @brief 分页请求 DTO（Page Request DTO），封装 page、size、sort 参数；
 *        Page-request DTO encapsulating page, size, and sort parameters.
 */
public record PageRequestDto(int page, int size, String sort) {

    /**
     * @brief 默认页码（Default Page Number）；
     *        Default page number.
     */
    public static final int DEFAULT_PAGE = 1;

    /**
     * @brief 默认每页条数（Default Page Size）；
     *        Default page size.
     */
    public static final int DEFAULT_SIZE = 20;

    /**
     * @brief 最大每页条数（Maximum Page Size）；
     *        Maximum allowed page size.
     */
    public static final int MAX_SIZE = 200;

    /**
     * @brief 规范化并校验分页请求（Normalize and Validate Page Request）；
     *        Normalize and validate page-request fields.
     */
    public PageRequestDto {
        if (page < 1) {
            throw new IllegalArgumentException("page must be greater than or equal to 1");
        }
        if (size < 1 || size > MAX_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and " + MAX_SIZE);
        }
        sort = Objects.requireNonNull(sort, "sort must not be null").trim();
    }

    /**
     * @brief 创建默认分页请求（Create Default Page Request）；
     *        Create default page request.
     *
     * @return 默认分页请求（Default page request）。
     */
    public static PageRequestDto defaults() {
        return new PageRequestDto(DEFAULT_PAGE, DEFAULT_SIZE, "");
    }

    /**
     * @brief 从查询参数构建分页请求（Build Page Request from Query Parameters）；
     *        Build page request from query parameters.
     *
     * @param request Web 请求（Web request）。
     * @return 分页请求 DTO（Page request DTO）。
     */
    public static PageRequestDto fromQuery(final WebRequest request) {
        final WebRequest normalizedRequest = Objects.requireNonNull(request, "request must not be null");
        final int page = parseIntOrDefault(normalizedRequest.queryParam("page").orElse(null), DEFAULT_PAGE, "page");
        final int size = parseIntOrDefault(normalizedRequest.queryParam("size").orElse(null), DEFAULT_SIZE, "size");
        final String sort = normalizedRequest.queryParam("sort").orElse("");
        return new PageRequestDto(page, size, sort);
    }

    /**
     * @brief 计算偏移量（Calculate Offset）；
     *        Calculate zero-based record offset.
     *
     * @return 偏移量（Offset）。
     */
    public int offset() {
        return (page - 1) * size;
    }

    /**
     * @brief 解析整数参数（Parse Integer Parameter）；
     *        Parse integer parameter with default fallback.
     *
     * @param rawValue 原始值（Raw value）。
     * @param defaultValue 默认值（Default value）。
     * @param fieldName 字段名（Field name）。
     * @return 解析后的整数（Parsed integer value）。
     */
    private static int parseIntOrDefault(final String rawValue, final int defaultValue, final String fieldName) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(rawValue.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(fieldName + " must be an integer", exception);
        }
    }
}

