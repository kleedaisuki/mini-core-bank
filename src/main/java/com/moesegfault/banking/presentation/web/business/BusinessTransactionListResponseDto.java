package com.moesegfault.banking.presentation.web.business;

import java.util.List;
import java.util.Objects;

/**
 * @brief 业务流水列表响应 DTO（Business Transaction List Response DTO），统一列表与总数输出；
 *        Business-transaction list response DTO unifying list items and total count output.
 */
public record BusinessTransactionListResponseDto(
        int total,
        List<BusinessTransactionResponseDto> items
) {

    /**
     * @brief 规范化并校验列表响应字段（Normalize and Validate List Response Fields）；
     *        Normalize and validate list-response fields.
     */
    public BusinessTransactionListResponseDto {
        if (total < 0) {
            throw new IllegalArgumentException("total must be greater than or equal to 0");
        }
        items = List.copyOf(Objects.requireNonNull(items, "items must not be null"));
    }
}

