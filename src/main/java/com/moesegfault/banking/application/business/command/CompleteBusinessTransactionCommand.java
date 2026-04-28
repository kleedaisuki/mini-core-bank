package com.moesegfault.banking.application.business.command;

import com.moesegfault.banking.domain.business.BusinessTransactionId;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 完成业务流水命令（Complete Business Transaction Command），封装交易终态流转输入；
 *        Complete-business-transaction command encapsulating terminal-state transition inputs.
 */
public final class CompleteBusinessTransactionCommand {

    /**
     * @brief 完成动作枚举（Completion Action Enum），表达交易终态目标；
     *        Completion action enum expressing target terminal state.
     */
    public enum CompletionAction {

        /**
         * @brief 成功完成（Success Completion）；
         *        Mark transaction as successful.
         */
        SUCCESS,

        /**
         * @brief 失败完成（Failed Completion）；
         *        Mark transaction as failed.
         */
        FAILED,

        /**
         * @brief 冲正完成（Reversed Completion）；
         *        Mark successful transaction as reversed.
         */
        REVERSED
    }

    /**
     * @brief 业务交易 ID（Business Transaction ID）；
     *        Business transaction ID.
     */
    private final String transactionId;

    /**
     * @brief 完成动作（Completion Action）；
     *        Completion action.
     */
    private final CompletionAction completionAction;

    /**
     * @brief 完成时间（可空）（Completed Timestamp, Nullable）；
     *        Completed timestamp, nullable.
     */
    private final Instant completedAt;

    /**
     * @brief 备注（可空）（Remarks, Nullable）；
     *        Remarks, nullable.
     */
    private final String remarks;

    /**
     * @brief 构造完成业务流水命令（Construct Complete-Business-Transaction Command）；
     *        Construct complete-business-transaction command.
     *
     * @param transactionId    交易 ID（Transaction ID）。
     * @param completionAction 完成动作（Completion action）。
     * @param completedAt      完成时间（Completed timestamp, nullable）。
     * @param remarks          备注（Remarks, nullable）。
     */
    public CompleteBusinessTransactionCommand(
            final String transactionId,
            final CompletionAction completionAction,
            final Instant completedAt,
            final String remarks
    ) {
        this.transactionId = Objects.requireNonNull(transactionId, "transactionId must not be null");
        this.completionAction = Objects.requireNonNull(completionAction, "completionAction must not be null");
        this.completedAt = completedAt;
        this.remarks = normalizeNullableText(remarks);
    }

    /**
     * @brief 返回交易 ID 原始值（Return Raw Transaction ID）；
     *        Return raw transaction ID.
     *
     * @return 交易 ID（Transaction ID）。
     */
    public String transactionId() {
        return transactionId;
    }

    /**
     * @brief 返回完成动作（Return Completion Action）；
     *        Return completion action.
     *
     * @return 完成动作（Completion action）。
     */
    public CompletionAction completionAction() {
        return completionAction;
    }

    /**
     * @brief 返回完成时间（Return Completed Timestamp）；
     *        Return completed timestamp, nullable.
     *
     * @return 完成时间或 null（Completed timestamp or null）。
     */
    public Instant completedAtOrNull() {
        return completedAt;
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
     * @brief 转换为业务交易 ID 值对象（Map to Business Transaction ID Value Object）；
     *        Map to business transaction ID value object.
     *
     * @return 业务交易 ID 值对象（Business transaction ID value object）。
     */
    public BusinessTransactionId toTransactionId() {
        return BusinessTransactionId.of(transactionId);
    }

    /**
     * @brief 解析完成时间（Resolve Completed Timestamp）；
     *        Resolve completion timestamp using command value or current instant.
     *
     * @return 完成时间（Resolved completion timestamp）。
     */
    public Instant resolveCompletedAt() {
        return completedAt == null ? Instant.now() : completedAt;
    }

    /**
     * @brief 标准化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text by trimming and collapsing blank to null.
     *
     * @param rawValue 原始文本（Raw text）。
     * @return 标准化文本或 null（Normalized text or null）。
     */
    private static String normalizeNullableText(final String rawValue) {
        if (rawValue == null) {
            return null;
        }
        final String normalized = rawValue.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}

