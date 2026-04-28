package com.moesegfault.banking.presentation.gui.account;

import java.util.List;

/**
 * @brief Account GUI 规范字段定义（Account GUI Schema Constants），统一页面字段名与表格列名；
 *        Account GUI schema constants that centralize field names and table columns.
 */
final class AccountGuiSchema {

    /**
     * @brief 客户 ID 字段（Customer ID Field）;
     *        Canonical field name for customer identifier.
     */
    static final String CUSTOMER_ID = "customer_id";

    /**
     * @brief 账户 ID 字段（Account ID Field）;
     *        Canonical field name for account identifier.
     */
    static final String ACCOUNT_ID = "account_id";

    /**
     * @brief 账户号字段（Account Number Field）;
     *        Canonical field name for account number.
     */
    static final String ACCOUNT_NO = "account_no";

    /**
     * @brief 绑定储蓄账户 ID 字段（Linked Savings Account ID Field）;
     *        Canonical field name for linked savings-account identifier.
     */
    static final String LINKED_SAVINGS_ACCOUNT_ID = "linked_savings_account_id";

    /**
     * @brief 包含已关闭账户字段（Include Closed Accounts Field）;
     *        Canonical field name for include-closed-accounts flag.
     */
    static final String INCLUDE_CLOSED_ACCOUNTS = "include_closed_accounts";

    /**
     * @brief 账户类型字段（Account Type Field）;
     *        Canonical field name for account type.
     */
    static final String ACCOUNT_TYPE = "account_type";

    /**
     * @brief 账户状态字段（Account Status Field）;
     *        Canonical field name for account status.
     */
    static final String ACCOUNT_STATUS = "account_status";

    /**
     * @brief 开户时间字段（Opened At Field）;
     *        Canonical field name for opened timestamp.
     */
    static final String OPENED_AT = "opened_at";

    /**
     * @brief 关户时间字段（Closed At Field）;
     *        Canonical field name for closed timestamp.
     */
    static final String CLOSED_AT = "closed_at";

    /**
     * @brief Account 列表列定义（Account Table Columns）;
     *        Canonical account table columns aligned with account output schema.
     */
    static final List<String> ACCOUNT_TABLE_COLUMNS = List.of(
            ACCOUNT_ID,
            CUSTOMER_ID,
            ACCOUNT_NO,
            ACCOUNT_TYPE,
            ACCOUNT_STATUS,
            OPENED_AT,
            CLOSED_AT,
            LINKED_SAVINGS_ACCOUNT_ID
    );

    /**
     * @brief 私有构造（Private Constructor）;
     *        Private constructor for utility class.
     */
    private AccountGuiSchema() {
    }
}
