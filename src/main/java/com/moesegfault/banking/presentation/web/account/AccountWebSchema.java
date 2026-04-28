package com.moesegfault.banking.presentation.web.account;

/**
 * @brief Account Web 字段规范（Account Web Schema Constants），统一 account 子域 Web 层字段命名；
 *        Account web schema constants centralizing field naming in account web subdomain.
 */
final class AccountWebSchema {

    /**
     * @brief 账户资源根路径（Accounts Root Path）；
     *        Root path for account resources.
     */
    static final String PATH_ACCOUNTS = "/accounts";

    /**
     * @brief 开立储蓄账户路径（Open Savings Account Path）；
     *        Path for opening savings account.
     */
    static final String PATH_OPEN_SAVINGS = "/accounts/savings";

    /**
     * @brief 开立外汇账户路径（Open FX Account Path）；
     *        Path for opening FX account.
     */
    static final String PATH_OPEN_FX = "/accounts/fx";

    /**
     * @brief 开立投资账户路径（Open Investment Account Path）；
     *        Path for opening investment account.
     */
    static final String PATH_OPEN_INVESTMENT = "/accounts/investment";

    /**
     * @brief 账户详情路径模式（Account Detail Path Pattern）；
     *        Path pattern for account detail.
     */
    static final String PATH_ACCOUNT_DETAIL = "/accounts/{accountId}";

    /**
     * @brief 按账户号查询路径模式（Account Lookup-by-No Path Pattern）；
     *        Path pattern for lookup by account number.
     */
    static final String PATH_ACCOUNT_BY_NO = "/accounts/by-account-no/{accountNo}";

    /**
     * @brief 路径参数名：账户 ID（Path Parameter Name: Account ID）；
     *        Path parameter name for account identifier.
     */
    static final String PATH_PARAM_ACCOUNT_ID = "accountId";

    /**
     * @brief 路径参数名：账户号（Path Parameter Name: Account Number）；
     *        Path parameter name for account number.
     */
    static final String PATH_PARAM_ACCOUNT_NO = "accountNo";

    /**
     * @brief 客户 ID 字段（Customer ID Field）；
     *        Canonical field name for customer identifier.
     */
    static final String CUSTOMER_ID = "customer_id";

    /**
     * @brief 账户 ID 字段（Account ID Field）；
     *        Canonical field name for account identifier.
     */
    static final String ACCOUNT_ID = "account_id";

    /**
     * @brief 账户号字段（Account Number Field）；
     *        Canonical field name for account number.
     */
    static final String ACCOUNT_NO = "account_no";

    /**
     * @brief 绑定储蓄账户 ID 字段（Linked Savings Account ID Field）；
     *        Canonical field name for linked savings-account identifier.
     */
    static final String LINKED_SAVINGS_ACCOUNT_ID = "linked_savings_account_id";

    /**
     * @brief 包含已关闭账户字段（Include Closed Accounts Field）；
     *        Canonical field name for include-closed-accounts flag.
     */
    static final String INCLUDE_CLOSED_ACCOUNTS = "include_closed_accounts";

    /**
     * @brief 账户状态字段（Account Status Field）；
     *        Canonical field name for account status.
     */
    static final String ACCOUNT_STATUS = "account_status";

    /**
     * @brief 冻结原因字段（Freeze Reason Field）；
     *        Canonical field name for freeze reason.
     */
    static final String FREEZE_REASON = "freeze_reason";

    /**
     * @brief 私有构造（Private Constructor）；
     *        Private constructor for utility class.
     */
    private AccountWebSchema() {
    }
}
