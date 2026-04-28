package com.moesegfault.banking.presentation.web.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.presentation.web.dto.PageResponseDto;
import java.util.List;
import java.util.Objects;

/**
 * @brief 客户列表响应 DTO（Customer List Response DTO），封装客户列表与分页元数据；
 *        Customer-list response DTO carrying customer items and paging metadata.
 */
public record CustomerListResponseDto(
        @JsonProperty("items") List<CustomerResponseDto> items,
        @JsonProperty("page") int page,
        @JsonProperty("size") int size,
        @JsonProperty("total") long total
) {

    /**
     * @brief 规范化并校验列表响应（Normalize and Validate List Response）；
     *        Normalize and validate customer-list response fields.
     */
    public CustomerListResponseDto {
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

    /**
     * @brief 从通用分页 DTO 转换（Map from Generic Page DTO）；
     *        Map from generic page response DTO to customer-list response DTO.
     *
     * @param pageResponse 通用分页响应（Generic page response）。
     * @return 客户列表响应（Customer-list response）。
     */
    public static CustomerListResponseDto fromPage(final PageResponseDto<CustomerResponseDto> pageResponse) {
        final PageResponseDto<CustomerResponseDto> normalized = Objects.requireNonNull(
                pageResponse,
                "pageResponse must not be null");
        return new CustomerListResponseDto(normalized.items(), normalized.page(), normalized.size(), normalized.total());
    }
}
