package com.moesegfault.banking.application.credit.query;

import java.time.LocalDate;

/**
 * @brief 查询账单请求（Find Statement Query），支持按账单 ID 或按“账户+账期”查询；
 *        Find-statement query supporting search by statement ID or by account-and-period.
 */
public final class FindStatementQuery {

    /**
     * @brief 账单 ID（可空）（Statement ID, Nullable）；
     *        Statement identifier, nullable.
     */
    private final String statementIdOrNull;

    /**
     * @brief 信用卡账户 ID（可空）（Credit Card Account ID, Nullable）；
     *        Credit-card-account identifier, nullable.
     */
    private final String creditCardAccountIdOrNull;

    /**
     * @brief 账期开始日期（可空）（Statement Period Start, Nullable）；
     *        Statement period start date, nullable.
     */
    private final LocalDate statementPeriodStartOrNull;

    /**
     * @brief 账期结束日期（可空）（Statement Period End, Nullable）；
     *        Statement period end date, nullable.
     */
    private final LocalDate statementPeriodEndOrNull;

    /**
     * @brief 构造查询请求（Construct Find Statement Query）；
     *        Construct find-statement query.
     *
     * @param statementIdOrNull 账单 ID（可空）（Statement ID, nullable）。
     * @param creditCardAccountIdOrNull 信用卡账户 ID（可空）（Credit-card-account ID, nullable）。
     * @param statementPeriodStartOrNull 账期开始日期（可空）（Statement period start, nullable）。
     * @param statementPeriodEndOrNull 账期结束日期（可空）（Statement period end, nullable）。
     */
    public FindStatementQuery(
            final String statementIdOrNull,
            final String creditCardAccountIdOrNull,
            final LocalDate statementPeriodStartOrNull,
            final LocalDate statementPeriodEndOrNull
    ) {
        this.statementIdOrNull = normalizeNullableText(statementIdOrNull);
        this.creditCardAccountIdOrNull = normalizeNullableText(creditCardAccountIdOrNull);
        this.statementPeriodStartOrNull = statementPeriodStartOrNull;
        this.statementPeriodEndOrNull = statementPeriodEndOrNull;
    }

    /**
     * @brief 创建“按账单 ID 查询”请求（Create Query by Statement ID）；
     *        Create query by statement identifier.
     *
     * @param statementId 账单 ID（Statement ID）。
     * @return 查询请求（Find-statement query）。
     */
    public static FindStatementQuery byStatementId(final String statementId) {
        return new FindStatementQuery(statementId, null, null, null);
    }

    /**
     * @brief 创建“按账户+账期查询”请求（Create Query by Account and Period）；
     *        Create query by account identifier and statement period.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param statementPeriodStart 账期开始日期（Statement period start）。
     * @param statementPeriodEnd 账期结束日期（Statement period end）。
     * @return 查询请求（Find-statement query）。
     */
    public static FindStatementQuery byPeriod(
            final String creditCardAccountId,
            final LocalDate statementPeriodStart,
            final LocalDate statementPeriodEnd
    ) {
        return new FindStatementQuery(null, creditCardAccountId, statementPeriodStart, statementPeriodEnd);
    }

    /**
     * @brief 返回账单 ID（可空）（Return Statement ID, Nullable）；
     *        Return statement identifier, nullable.
     *
     * @return 账单 ID 或 null（Statement ID or null）。
     */
    public String statementIdOrNull() {
        return statementIdOrNull;
    }

    /**
     * @brief 返回信用卡账户 ID（可空）（Return Credit Card Account ID, Nullable）；
     *        Return credit-card-account identifier, nullable.
     *
     * @return 信用卡账户 ID 或 null（Credit-card-account ID or null）。
     */
    public String creditCardAccountIdOrNull() {
        return creditCardAccountIdOrNull;
    }

    /**
     * @brief 返回账期开始日期（可空）（Return Statement Period Start, Nullable）；
     *        Return statement period start date, nullable.
     *
     * @return 账期开始日期或 null（Statement period start or null）。
     */
    public LocalDate statementPeriodStartOrNull() {
        return statementPeriodStartOrNull;
    }

    /**
     * @brief 返回账期结束日期（可空）（Return Statement Period End, Nullable）；
     *        Return statement period end date, nullable.
     *
     * @return 账期结束日期或 null（Statement period end or null）。
     */
    public LocalDate statementPeriodEndOrNull() {
        return statementPeriodEndOrNull;
    }

    /**
     * @brief 标准化可空文本（Normalize Nullable Text）；
     *        Normalize nullable text.
     *
     * @param value 输入值（Input value）。
     * @return 标准化文本或 null（Normalized text or null）。
     */
    private static String normalizeNullableText(final String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
