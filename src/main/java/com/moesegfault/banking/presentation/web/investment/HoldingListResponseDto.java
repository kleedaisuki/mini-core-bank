package com.moesegfault.banking.presentation.web.investment;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Objects;

/**
 * @brief 持仓列表响应 DTO（Holding List Response DTO），封装持仓列表与查询上下文；
 *        Holding-list response DTO carrying holdings and query metadata.
 */
public record HoldingListResponseDto(
        @JsonProperty("investment_account_id") String investmentAccountId,
        @JsonProperty("include_product_details") boolean includeProductDetails,
        @JsonProperty("total") int total,
        @JsonProperty("items") List<HoldingResponseDto> items
) {

    /**
     * @brief 规范化并校验响应字段（Normalize and Validate Response Fields）；
     *        Normalize and validate holding-list response fields.
     */
    public HoldingListResponseDto {
        investmentAccountId = InvestmentWebPayloadParser.normalizeRequiredText(
                investmentAccountId,
                "investment_account_id");
        if (total < 0) {
            throw new IllegalArgumentException("total must be greater than or equal to 0");
        }
        items = List.copyOf(Objects.requireNonNull(items, "items must not be null"));
    }
}
