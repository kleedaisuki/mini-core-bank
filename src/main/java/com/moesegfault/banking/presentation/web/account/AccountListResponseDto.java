package com.moesegfault.banking.presentation.web.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.account.result.AccountResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @brief 账户列表响应 DTO（Account List Response DTO），统一账户列表返回结构；
 *        Account-list response DTO standardizing list response structure.
 */
public record AccountListResponseDto(
        @JsonProperty("items") List<AccountResponseDto> items,
        @JsonProperty("total") long total
) {

    /**
     * @brief 规范化并校验列表响应（Normalize and Validate List Response）；
     *        Normalize and validate account-list response fields.
     */
    public AccountListResponseDto {
        items = List.copyOf(Objects.requireNonNull(items, "items must not be null"));
        if (total < 0) {
            throw new IllegalArgumentException("total must not be negative");
        }
    }

    /**
     * @brief 从账户结果列表构建响应（Build Response from Account Results）；
     *        Build account-list response from account-result list.
     *
     * @param results 账户结果列表（Account-result list）。
     * @return 列表响应（Account-list response DTO）。
     */
    public static AccountListResponseDto fromResults(final List<AccountResult> results) {
        final List<AccountResult> normalizedResults = List.copyOf(Objects.requireNonNull(results, "results must not be null"));
        final List<AccountResponseDto> items = new ArrayList<>(normalizedResults.size());
        for (AccountResult result : normalizedResults) {
            items.add(AccountResponseDto.from(result));
        }
        return new AccountListResponseDto(items, items.size());
    }
}

