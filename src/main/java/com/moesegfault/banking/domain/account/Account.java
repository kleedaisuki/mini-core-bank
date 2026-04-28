package com.moesegfault.banking.domain.account;

import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.time.Instant;
import java.util.Objects;

/**
 * @brief 账户基础实体（Account Base Entity），映射 `account` 表并维护核心状态不变量（Invariant）；
 *        Account base entity mapped to `account` table and enforcing core state invariants.
 */
public final class Account {

    /**
     * @brief 账户 ID（Account Identifier）；
     *        Account identifier.
     */
    private final AccountId accountId;

    /**
     * @brief 账户所属客户 ID（Owner Customer ID）；
     *        Owner customer identifier.
     */
    private final CustomerId customerId;

    /**
     * @brief 账户号（Account Number）；
     *        Account number.
     */
    private final AccountNumber accountNo;

    /**
     * @brief 账户类型（Account Type）；
     *        Account type.
     */
    private final AccountType accountType;

    /**
     * @brief 账户状态（Account Status）；
     *        Account status.
     */
    private AccountStatus accountStatus;

    /**
     * @brief 开户时间（Opened Timestamp）；
     *        Opened timestamp.
     */
    private final Instant openedAt;

    /**
     * @brief 关闭时间（可空）（Closed Timestamp, Nullable）；
     *        Closed timestamp, nullable.
     */
    private Instant closedAt;

    /**
     * @brief 构造账户实体（Construct Account Entity）；
     *        Construct account entity.
     *
     * @param accountId     账户 ID（Account ID）。
     * @param customerId    所属客户 ID（Owner customer ID）。
     * @param accountNo     账户号（Account number）。
     * @param accountType   账户类型（Account type）。
     * @param accountStatus 账户状态（Account status）。
     * @param openedAt      开户时间（Opened timestamp）。
     * @param closedAt      关闭时间（Closed timestamp, nullable）。
     */
    private Account(
            final AccountId accountId,
            final CustomerId customerId,
            final AccountNumber accountNo,
            final AccountType accountType,
            final AccountStatus accountStatus,
            final Instant openedAt,
            final Instant closedAt
    ) {
        this.accountId = Objects.requireNonNull(accountId, "Account ID must not be null");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID must not be null");
        this.accountNo = Objects.requireNonNull(accountNo, "Account number must not be null");
        this.accountType = Objects.requireNonNull(accountType, "Account type must not be null");
        this.accountStatus = Objects.requireNonNull(accountStatus, "Account status must not be null");
        this.openedAt = Objects.requireNonNull(openedAt, "Opened-at must not be null");
        this.closedAt = closedAt;
        validateTimeline(this.openedAt, this.closedAt);
        validateStatusAndClosedAt(this.accountStatus, this.closedAt);
    }

    /**
     * @brief 新开户（Open New Account）；
     *        Open a new account.
     *
     * @param accountId   账户 ID（Account ID）。
     * @param customerId  所属客户 ID（Owner customer ID）。
     * @param accountNo   账户号（Account number）。
     * @param accountType 账户类型（Account type）。
     * @return 新建账户实体（New account entity）。
     */
    public static Account open(
            final AccountId accountId,
            final CustomerId customerId,
            final AccountNumber accountNo,
            final AccountType accountType
    ) {
        final Instant now = Instant.now();
        return new Account(
                accountId,
                customerId,
                accountNo,
                accountType,
                AccountStatus.ACTIVE,
                now,
                null);
    }

    /**
     * @brief 从持久化状态重建账户（Restore Account from Persistence）；
     *        Restore account from persistence state.
     *
     * @param accountId     账户 ID（Account ID）。
     * @param customerId    所属客户 ID（Owner customer ID）。
     * @param accountNo     账户号（Account number）。
     * @param accountType   账户类型（Account type）。
     * @param accountStatus 账户状态（Account status）。
     * @param openedAt      开户时间（Opened timestamp）。
     * @param closedAt      关闭时间（Closed timestamp, nullable）。
     * @return 重建账户实体（Restored account entity）。
     */
    public static Account restore(
            final AccountId accountId,
            final CustomerId customerId,
            final AccountNumber accountNo,
            final AccountType accountType,
            final AccountStatus accountStatus,
            final Instant openedAt,
            final Instant closedAt
    ) {
        return new Account(
                accountId,
                customerId,
                accountNo,
                accountType,
                accountStatus,
                openedAt,
                closedAt);
    }

    /**
     * @brief 冻结账户（Freeze Account）；
     *        Freeze account.
     */
    public void freeze() {
        if (accountStatus == AccountStatus.FROZEN) {
            return;
        }
        if (accountStatus != AccountStatus.ACTIVE) {
            throw new BusinessRuleViolation(
                    "Only ACTIVE account can be frozen, current status: " + accountStatus);
        }
        this.accountStatus = AccountStatus.FROZEN;
    }

    /**
     * @brief 解冻账户（Activate Frozen Account）；
     *        Activate a frozen account.
     */
    public void activate() {
        if (accountStatus == AccountStatus.ACTIVE) {
            return;
        }
        if (accountStatus != AccountStatus.FROZEN) {
            throw new BusinessRuleViolation(
                    "Only FROZEN account can be activated, current status: " + accountStatus);
        }
        this.accountStatus = AccountStatus.ACTIVE;
    }

    /**
     * @brief 按当前时间关闭账户（Close Account at Now）；
     *        Close account at current timestamp.
     */
    public void close() {
        closeAt(Instant.now());
    }

    /**
     * @brief 按指定时间关闭账户（Close Account at Specific Time）；
     *        Close account at a specific timestamp.
     *
     * @param closedAtTimestamp 关闭时间（Closed timestamp）。
     */
    public void closeAt(final Instant closedAtTimestamp) {
        if (accountStatus == AccountStatus.CLOSED) {
            return;
        }
        final Instant normalizedClosedAt = Objects.requireNonNull(
                closedAtTimestamp,
                "Closed-at timestamp must not be null");
        validateTimeline(openedAt, normalizedClosedAt);
        this.accountStatus = AccountStatus.CLOSED;
        this.closedAt = normalizedClosedAt;
    }

    /**
     * @brief 返回账户 ID（Return Account ID）；
     *        Return account identifier.
     *
     * @return 账户 ID（Account ID）。
     */
    public AccountId accountId() {
        return accountId;
    }

    /**
     * @brief 返回所属客户 ID（Return Owner Customer ID）；
     *        Return owner customer identifier.
     *
     * @return 所属客户 ID（Owner customer ID）。
     */
    public CustomerId customerId() {
        return customerId;
    }

    /**
     * @brief 返回账户号（Return Account Number）；
     *        Return account number.
     *
     * @return 账户号（Account number）。
     */
    public AccountNumber accountNo() {
        return accountNo;
    }

    /**
     * @brief 返回账户类型（Return Account Type）；
     *        Return account type.
     *
     * @return 账户类型（Account type）。
     */
    public AccountType accountType() {
        return accountType;
    }

    /**
     * @brief 返回账户状态（Return Account Status）；
     *        Return account status.
     *
     * @return 账户状态（Account status）。
     */
    public AccountStatus accountStatus() {
        return accountStatus;
    }

    /**
     * @brief 返回开户时间（Return Opened Timestamp）；
     *        Return opened timestamp.
     *
     * @return 开户时间（Opened timestamp）。
     */
    public Instant openedAt() {
        return openedAt;
    }

    /**
     * @brief 返回关闭时间（可空）（Return Closed Timestamp, Nullable）；
     *        Return closed timestamp, nullable.
     *
     * @return 关闭时间或 null（Closed timestamp or null）。
     */
    public Instant closedAtOrNull() {
        return closedAt;
    }

    /**
     * @brief 校验时间线（Validate Timeline）；
     *        Validate opened/closed timeline.
     *
     * @param openedAtTimestamp 开户时间（Opened timestamp）。
     * @param closedAtTimestamp 关闭时间（Closed timestamp, nullable）。
     */
    private static void validateTimeline(
            final Instant openedAtTimestamp,
            final Instant closedAtTimestamp
    ) {
        if (closedAtTimestamp != null && closedAtTimestamp.isBefore(openedAtTimestamp)) {
            throw new BusinessRuleViolation("Account closed-at must not be before opened-at");
        }
    }

    /**
     * @brief 校验状态与关闭时间的一致性（Validate Status-ClosedAt Consistency）；
     *        Validate consistency between account status and closed-at.
     *
     * @param status             账户状态（Account status）。
     * @param closedAtTimestamp  关闭时间（Closed timestamp, nullable）。
     */
    private static void validateStatusAndClosedAt(
            final AccountStatus status,
            final Instant closedAtTimestamp
    ) {
        if (status == AccountStatus.CLOSED && closedAtTimestamp == null) {
            throw new BusinessRuleViolation("CLOSED account must have closed-at timestamp");
        }
        if (status != AccountStatus.CLOSED && closedAtTimestamp != null) {
            throw new BusinessRuleViolation("Only CLOSED account can have closed-at timestamp");
        }
    }
}
