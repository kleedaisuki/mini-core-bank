package com.moesegfault.banking.presentation.web.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.moesegfault.banking.application.customer.command.RegisterCustomerCommand;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import java.util.Objects;

/**
 * @brief 注册客户请求 DTO（Register Customer Request DTO），映射 POST /customers 的请求体；
 *        Register-customer request DTO mapped to POST /customers request body.
 */
public record RegisterCustomerRequestDto(
        @JsonProperty("id_type") String idType,
        @JsonProperty("id_number") String idNumber,
        @JsonProperty("issuing_region") String issuingRegion,
        @JsonProperty("mobile_phone") String mobilePhone,
        @JsonProperty("residential_address") String residentialAddress,
        @JsonProperty("mailing_address") String mailingAddress,
        @JsonProperty("is_us_tax_resident") Boolean isUsTaxResident,
        @JsonProperty("crs_info") String crsInfo
) {

    /**
     * @brief 规范化并校验请求字段（Normalize and Validate Request Fields）；
     *        Normalize and validate register-customer request fields.
     */
    public RegisterCustomerRequestDto {
        idType = normalizeRequiredText(idType, "id_type");
        idNumber = normalizeRequiredText(idNumber, "id_number");
        issuingRegion = normalizeRequiredText(issuingRegion, "issuing_region");
        mobilePhone = normalizeRequiredText(mobilePhone, "mobile_phone");
        residentialAddress = normalizeRequiredText(residentialAddress, "residential_address");
        mailingAddress = normalizeRequiredText(mailingAddress, "mailing_address");
        isUsTaxResident = Objects.requireNonNull(isUsTaxResident, "is_us_tax_resident must not be null");
        crsInfo = normalizeOptionalText(crsInfo);
    }

    /**
     * @brief 转换为应用层命令（Map to Application Command）；
     *        Map request DTO to register-customer application command.
     *
     * @return 注册客户命令（Register-customer command）。
     */
    public RegisterCustomerCommand toCommand() {
        return new RegisterCustomerCommand(
                IdentityDocumentType.fromDatabaseValue(idType),
                idNumber,
                issuingRegion,
                mobilePhone,
                residentialAddress,
                mailingAddress,
                isUsTaxResident,
                crsInfo);
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
