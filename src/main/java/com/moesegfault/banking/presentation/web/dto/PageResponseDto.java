package com.moesegfault.banking.presentation.web.dto;

import java.util.List;
import java.util.Objects;

/**
 * @brief 分页响应 DTO（Page Response DTO），封装列表、分页信息与总条数；
 *        Page-response DTO carrying items, paging metadata, and total count.
 *
 * @param <T> 项目类型（Item type）。
 */
public record PageResponseDto<T>(List<T> items, int page, int size, long total) {

    /**
     * @brief 规范化并校验分页响应（Normalize and Validate Page Response）；
     *        Normalize and validate page-response fields.
     */
    public PageResponseDto {
        items = List.copyOf(Objects.requireNonNull(items, "items must not be null"));
        if (page < 1) {
            throw new IllegalArgumentException("page must be greater than or equal to 1");
        }
        if (size < 1) {
            throw new IllegalArgumentException("size must be greater than or equal to 1");
        }
        if (total < 0) {
            throw new IllegalArgumentException("total must be greater than or equal to 0");
        }
    }
}

