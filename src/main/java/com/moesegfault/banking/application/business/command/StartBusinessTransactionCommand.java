package com.moesegfault.banking.application.business.command;

import com.moesegfault.banking.domain.business.BusinessChannel;
import com.moesegfault.banking.domain.business.BusinessReference;
import com.moesegfault.banking.domain.business.BusinessTypeCode;
import com.moesegfault.banking.domain.business.CustomerId;
import java.util.Objects;

/**
 * @brief 开始业务流水命令（Start Business Transaction Command），封装统一业务流水创建输入；
 *        Start-business-transaction command encapsulating creation inputs of unified business flow.
 */
public final class StartBusinessTransactionCommand {

    /**
     * @brief 业务类型码（Business Type Code）；
     *        Business type code.
     */
    private final String businessTypeCode;

    /**
     * @brief 发起客户 ID（可空）（Initiator Customer ID, Nullable）；
     *        Initiator customer ID, nullable.
     */
    private final String initiatorCustomerId;

    /**
     * @brief 操作员 ID（可空）（Operator ID, Nullable）；
     *        Operator ID, nullable.
     */
    private final String operatorId;

    /**
     * @brief 发起渠道（Business Channel）；
     *        Business channel.
     */
    private final BusinessChannel channel;

    /**
     * @brief 业务参考号（Business Reference Number）；
     *        Business reference number.
     */
    private final String referenceNo;

    /**
     * @brief 备注（可空）（Remarks, Nullable）；
     *        Remarks, nullable.
     */
    private final String remarks;

    /**
     * @brief 构造开始业务流水命令（Construct Start-Business-Transaction Command）；
     *        Construct start-business-transaction command.
     *
     * @param businessTypeCode    业务类型码（Business type code）。
     * @param initiatorCustomerId 发起客户 ID（Initiator customer ID, nullable）。
     * @param operatorId          操作员 ID（Operator ID, nullable）。
     * @param channel             渠道（Channel）。
     * @param referenceNo         业务参考号（Business reference number）。
     * @param remarks             备注（Remarks, nullable）。
     */
    public StartBusinessTransactionCommand(
            final String businessTypeCode,
            final String initiatorCustomerId,
            final String operatorId,
            final BusinessChannel channel,
            final String referenceNo,
            final String remarks
    ) {
        this.businessTypeCode = Objects.requireNonNull(businessTypeCode, "businessTypeCode must not be null");
        this.initiatorCustomerId = normalizeNullableText(initiatorCustomerId);
        this.operatorId = normalizeNullableText(operatorId);
        this.channel = Objects.requireNonNull(channel, "channel must not be null");
        this.referenceNo = Objects.requireNonNull(referenceNo, "referenceNo must not be null");
        this.remarks = normalizeNullableText(remarks);
    }

    /**
     * @brief 返回业务类型码原始值（Return Raw Business Type Code）；
     *        Return raw business type code.
     *
     * @return 业务类型码（Business type code）。
     */
    public String businessTypeCode() {
        return businessTypeCode;
    }

    /**
     * @brief 返回发起客户 ID（Return Initiator Customer ID）；
     *        Return initiator customer ID, nullable.
     *
     * @return 发起客户 ID 或 null（Initiator customer ID or null）。
     */
    public String initiatorCustomerIdOrNull() {
        return initiatorCustomerId;
    }

    /**
     * @brief 返回操作员 ID（Return Operator ID）；
     *        Return operator ID, nullable.
     *
     * @return 操作员 ID 或 null（Operator ID or null）。
     */
    public String operatorIdOrNull() {
        return operatorId;
    }

    /**
     * @brief 返回业务渠道（Return Business Channel）；
     *        Return business channel.
     *
     * @return 业务渠道（Business channel）。
     */
    public BusinessChannel channel() {
        return channel;
    }

    /**
     * @brief 返回业务参考号原始值（Return Raw Business Reference Number）；
     *        Return raw business reference number.
     *
     * @return 业务参考号（Business reference number）。
     */
    public String referenceNo() {
        return referenceNo;
    }

    /**
     * @brief 返回备注（Return Remarks）；
     *        Return remarks, nullable.
     *
     * @return 备注或 null（Remarks or null）。
     */
    public String remarksOrNull() {
        return remarks;
    }

    /**
     * @brief 转换为业务类型码值对象（Map to Business Type Code Value Object）；
     *        Map to business type code value object.
     *
     * @return 业务类型码值对象（Business type code value object）。
     */
    public BusinessTypeCode toBusinessTypeCode() {
        return BusinessTypeCode.of(businessTypeCode);
    }

    /**
     * @brief 转换为发起客户 ID 值对象（Map to Initiator Customer ID Value Object）；
     *        Map to initiator customer ID value object, nullable.
     *
     * @return 客户 ID 值对象或 null（Customer ID value object or null）。
     */
    public CustomerId toInitiatorCustomerIdOrNull() {
        if (initiatorCustomerId == null) {
            return null;
        }
        return CustomerId.of(initiatorCustomerId);
    }

    /**
     * @brief 转换为业务参考号值对象（Map to Business Reference Value Object）；
     *        Map to business reference value object.
     *
     * @return 业务参考号值对象（Business reference value object）。
     */
    public BusinessReference toReferenceNo() {
        return BusinessReference.of(referenceNo);
    }

    /**
     * @brief 标准化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and collapsing blank to null.
     *
     * @param rawValue 原始值（Raw value）。
     * @return 标准化值或 null（Normalized value or null）。
     */
    private static String normalizeNullableText(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}

