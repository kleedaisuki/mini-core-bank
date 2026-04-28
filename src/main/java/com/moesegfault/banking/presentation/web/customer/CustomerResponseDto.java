package com.moesegfault.banking.presentation.web.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.customer.result.CustomerResult;
import java.util.Objects;

/**
 * @brief 客户响应 DTO（Customer Response DTO），统一客户详情与创建成功响应结构；
 *        Customer response DTO unifying customer-detail and create-success response schema.
 */
public record CustomerResponseDto(
        @JsonProperty("customer_id") String customerId,
        @JsonProperty("id_type") String idType,
        @JsonProperty("id_number") String idNumber,
        @JsonProperty("issuing_region") String issuingRegion,
        @JsonProperty("mobile_phone") String mobilePhone,
        @JsonProperty("residential_address") String residentialAddress,
        @JsonProperty("mailing_address") String mailingAddress,
        @JsonProperty("is_us_tax_resident") boolean isUsTaxResident,
        @JsonProperty("crs_info") String crsInfo,
        @JsonProperty("customer_status") String customerStatus,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("updated_at") String updatedAt
) {

    /**
     * @brief 规范化并校验响应字段（Normalize and Validate Response Fields）；
     *        Normalize and validate customer-response fields.
     */
    public CustomerResponseDto {
        customerId = normalizeRequiredText(customerId, "customer_id");
        idType = normalizeRequiredText(idType, "id_type");
        idNumber = normalizeRequiredText(idNumber, "id_number");
        issuingRegion = normalizeRequiredText(issuingRegion, "issuing_region");
        mobilePhone = normalizeRequiredText(mobilePhone, "mobile_phone");
        residentialAddress = normalizeRequiredText(residentialAddress, "residential_address");
        mailingAddress = normalizeRequiredText(mailingAddress, "mailing_address");
        customerStatus = normalizeRequiredText(customerStatus, "customer_status");
        createdAt = normalizeRequiredText(createdAt, "created_at");
        updatedAt = normalizeRequiredText(updatedAt, "updated_at");
        crsInfo = normalizeOptionalText(crsInfo);
    }

    /**
     * @brief 从应用层结果映射响应 DTO（Map from Application Result）；
     *        Map application customer result to response DTO.
     *
     * @param result 应用层客户结果（Application customer result）。
     * @return 客户响应 DTO（Customer response DTO）。
     */
    public static CustomerResponseDto fromResult(final CustomerResult result) {
        final CustomerResult normalizedResult = Objects.requireNonNull(result, "result must not be null");
        return new CustomerResponseDto(
                normalizedResult.customerId(),
                normalizedResult.idType(),
                normalizedResult.idNumber(),
                normalizedResult.issuingRegion(),
                normalizedResult.mobilePhone(),
                normalizedResult.residentialAddress(),
                normalizedResult.mailingAddress(),
                normalizedResult.usTaxResident(),
                normalizedResult.crsInfo(),
                normalizedResult.customerStatus(),
                normalizedResult.createdAt().toString(),
                normalizedResult.updatedAt().toString());
    }

    /**
     * @brief 规范化必填文本（Normalize Required Text）；
     *        Normalize required text field and reject blank value.
     *
     * @param rawValue 原始值（Raw value）。
     * @param fieldName 字段名（Field name）。
     * @return 规范化文本（Normalized text）。
     */
    private static String normalizeRequiredText(final String rawValue, final String fieldName) {
        final String normalized = Objects.requireNonNull(rawValue, fieldName + " must not be null").trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be blank");
        }
        return normalized;
    }

    /**
     * @brief 规范化可选文本（Normalize Optional Text）；
     *        Normalize optional text field and convert blank to null.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 规范化文本（Normalized text, nullable）。
     */
    private static String normalizeOptionalText(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
