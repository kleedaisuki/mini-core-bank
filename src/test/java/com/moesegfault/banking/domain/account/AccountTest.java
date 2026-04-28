package com.moesegfault.banking.domain.account;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.moesegfault.banking.domain.customer.CustomerId;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * @brief Account 领域单元测试（Account Domain Unit Test），覆盖开户、状态流转与关键策略约束；
 *        Account domain unit tests covering opening, lifecycle transitions, and key policy constraints.
 */
class AccountTest {

    /**
     * @brief 验证储蓄账户开户默认激活并产出开户事件；
     *        Verify savings-account opening defaults to ACTIVE and emits opened event.
     */
    @Test
    void shouldOpenSavingsAccountAsActiveAndEmitEvent() {
        final SavingsAccount savingsAccount = SavingsAccount.open(
                SavingsAccountId.of("sav-001"),
                CustomerId.of("cust-001"),
                AccountNumber.of("SA-20260428-001"));

        assertEquals(AccountType.SAVINGS, savingsAccount.account().accountType());
        assertEquals(AccountStatus.ACTIVE, savingsAccount.account().accountStatus());
        assertNotNull(savingsAccount.account().openedAt());

        final SavingsAccountOpened event = savingsAccount.openedEvent();
        assertEquals(SavingsAccountId.of("sav-001"), event.savingsAccountId());
        assertEquals(CustomerId.of("cust-001"), event.customerId());
        assertEquals(AccountNumber.of("SA-20260428-001"), event.accountNo());
    }

    /**
     * @brief 验证账户状态流转：冻结、解冻、关闭；
     *        Verify account status transitions: freeze, activate, and close.
     */
    @Test
    void shouldTransitionAccountStatusByLifecycleOperations() {
        final Account account = Account.open(
                AccountId.of("acc-001"),
                CustomerId.of("cust-100"),
                AccountNumber.of("ACC-100-001"),
                AccountType.FX);

        account.freeze();
        assertEquals(AccountStatus.FROZEN, account.accountStatus());

        account.activate();
        assertEquals(AccountStatus.ACTIVE, account.accountStatus());

        final Instant closedAt = account.openedAt().plusSeconds(60);
        account.closeAt(closedAt);
        assertEquals(AccountStatus.CLOSED, account.accountStatus());
        assertEquals(closedAt, account.closedAtOrNull());

        assertThrows(BusinessRuleViolation.class, account::freeze);
    }

    /**
     * @brief 验证账户恢复时状态与关闭时间必须一致；
     *        Verify restored account must keep status and closed-at consistency.
     */
    @Test
    void shouldRejectInvalidRestoreStateCombinations() {
        final Instant openedAt = Instant.parse("2026-04-28T00:00:00Z");

        assertThrows(
                BusinessRuleViolation.class,
                () -> Account.restore(
                        AccountId.of("acc-r-1"),
                        CustomerId.of("cust-r-1"),
                        AccountNumber.of("ACC-R-1"),
                        AccountType.SAVINGS,
                        AccountStatus.CLOSED,
                        openedAt,
                        null));

        assertThrows(
                BusinessRuleViolation.class,
                () -> Account.restore(
                        AccountId.of("acc-r-2"),
                        CustomerId.of("cust-r-2"),
                        AccountNumber.of("ACC-R-2"),
                        AccountType.FX,
                        AccountStatus.ACTIVE,
                        openedAt,
                        openedAt.plusSeconds(1)));

        assertThrows(
                BusinessRuleViolation.class,
                () -> Account.restore(
                        AccountId.of("acc-r-3"),
                        CustomerId.of("cust-r-3"),
                        AccountNumber.of("ACC-R-3"),
                        AccountType.INVESTMENT,
                        AccountStatus.CLOSED,
                        openedAt,
                        openedAt.minusSeconds(1)));
    }

    /**
     * @brief 验证外汇账户不能将自己作为绑定储蓄账户；
     *        Verify FX account cannot link itself as the savings account.
     */
    @Test
    void shouldRejectFxAccountSelfLinking() {
        assertThrows(
                BusinessRuleViolation.class,
                () -> FxAccount.open(
                        FxAccountId.of("acc-shared-001"),
                        CustomerId.of("cust-shared-001"),
                        AccountNumber.of("FX-SELF-001"),
                        SavingsAccountId.of("acc-shared-001")));
    }

    /**
     * @brief 验证外汇账户与储蓄账户绑定归属和链接一致性策略；
     *        Verify FX-savings ownership and linkage consistency policy.
     */
    @Test
    void shouldEnforceFxLinkedSavingsOwnershipPolicy() {
        final SavingsAccount savingsA = SavingsAccount.open(
                SavingsAccountId.of("sav-a-001"),
                CustomerId.of("cust-a"),
                AccountNumber.of("SA-A-001"));

        final FxAccount fxWrongOwner = FxAccount.open(
                FxAccountId.of("fx-b-001"),
                CustomerId.of("cust-b"),
                AccountNumber.of("FX-B-001"),
                SavingsAccountId.of("sav-a-001"));

        assertThrows(
                BusinessRuleViolation.class,
                () -> AccountPolicy.ensureFxLinkedSavingsOwnership(fxWrongOwner, savingsA));

        final FxAccount fxWrongLink = FxAccount.open(
                FxAccountId.of("fx-a-002"),
                CustomerId.of("cust-a"),
                AccountNumber.of("FX-A-002"),
                SavingsAccountId.of("sav-a-999"));

        assertThrows(
                BusinessRuleViolation.class,
                () -> AccountPolicy.ensureFxLinkedSavingsOwnership(fxWrongLink, savingsA));

        final FxAccount fxValid = FxAccount.open(
                FxAccountId.of("fx-a-003"),
                CustomerId.of("cust-a"),
                AccountNumber.of("FX-A-003"),
                SavingsAccountId.of("sav-a-001"));

        assertDoesNotThrow(() -> AccountPolicy.ensureFxLinkedSavingsOwnership(fxValid, savingsA));
    }

    /**
     * @brief 验证投资账户数量上限策略；
     *        Verify investment-account count limit policy.
     */
    @Test
    void shouldEnforceSingleInvestmentAccountPerCustomerPolicy() {
        assertDoesNotThrow(() -> AccountPolicy.ensureSingleInvestmentAccountPerCustomer(0));
        assertThrows(
                BusinessRuleViolation.class,
                () -> AccountPolicy.ensureSingleInvestmentAccountPerCustomer(1));
        assertThrows(
                IllegalArgumentException.class,
                () -> AccountPolicy.ensureSingleInvestmentAccountPerCustomer(-1));
    }
}
