package com.moesegfault.banking.domain.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * @brief Card 领域单元测试（Card Domain Unit Test），覆盖卡号、状态流转与主副卡规则；
 *        Card domain unit tests covering card number, status transitions, and primary-supplementary rules.
 */
class CardTest {

    /**
     * @brief 验证卡号归一化与脱敏显示；
     *        Verify card-number normalization and masked rendering.
     */
    @Test
    void shouldNormalizeAndMaskCardNumber() {
        final CardNumber cardNumber = CardNumber.of("6222-1234 5678 9012");

        assertEquals("6222123456789012", cardNumber.value());
        assertEquals("************9012", cardNumber.masked());
        assertThrows(IllegalArgumentException.class, () -> CardNumber.of("ABC"));
    }

    /**
     * @brief 验证扣账卡发行默认激活并产出发卡事件；
     *        Verify debit-card issuance defaults to ACTIVE and emits issued event.
     */
    @Test
    void shouldIssueDebitCardAsActiveAndEmitEvent() {
        final DebitCard debitCard = DebitCard.issue(
                CardId.of("debit-001"),
                CardNumber.of("6222333344445555"),
                CustomerId.of("cust-001"),
                DebitCardBinding.of(SavingsAccountId.of("sav-001"), FxAccountId.of("fx-001")));

        assertEquals(CardStatus.ACTIVE, debitCard.cardStatus());
        assertNotNull(debitCard.issuedAt());
        assertNull(debitCard.expiredAtOrNull());

        final DebitCardIssued event = debitCard.issuedEvent();
        assertEquals(CardId.of("debit-001"), event.cardId());
        assertEquals(CustomerId.of("cust-001"), event.holderCustomerId());
        assertEquals(SavingsAccountId.of("sav-001"), event.savingsAccountId());
    }

    /**
     * @brief 验证有效期约束：过期时间必须晚于发卡时间；
     *        Verify expiry constraint: expired-at must be later than issued-at.
     */
    @Test
    void shouldRejectExpiryNotAfterIssuedAt() {
        final Instant issuedAt = Instant.parse("2026-04-28T00:00:00Z");

        assertThrows(BusinessRuleViolation.class, () -> CardExpiry.of(issuedAt, issuedAt));
        assertThrows(BusinessRuleViolation.class, () -> CardExpiry.of(issuedAt, issuedAt.minusSeconds(1)));
    }

    /**
     * @brief 验证信用卡主副卡角色与父卡关联约束；
     *        Verify credit-card role and primary-card linkage constraints.
     */
    @Test
    void shouldEnforceCreditCardRoleParentConsistency() {
        final CardId primaryId = CardId.of("cc-primary-001");
        final CreditCard primary = CreditCard.issuePrimary(
                primaryId,
                CardNumber.of("4111222233334444"),
                CustomerId.of("cust-010"),
                CreditCardAccountId.of("cc-acc-001"));

        assertEquals(CardRole.PRIMARY, primary.cardRole());
        assertNull(primary.primaryCreditCardIdOrNull());

        final CreditCard supplementary = CreditCard.issueSupplementary(
                CardId.of("cc-sup-001"),
                CardNumber.of("4111222233335555"),
                CustomerId.of("cust-011"),
                CreditCardAccountId.of("cc-acc-001"),
                primaryId);

        assertTrue(supplementary.isSupplementary());
        assertEquals(primaryId, supplementary.primaryCreditCardIdOrNull());
    }

    /**
     * @brief 验证附属卡策略会拒绝非激活主卡；
     *        Verify supplementary policy rejects non-active primary card.
     */
    @Test
    void shouldRejectSupplementaryIssuanceWhenPrimaryNotActive() {
        final DebitCard primaryDebit = DebitCard.issue(
                CardId.of("debit-primary-001"),
                CardNumber.of("6222999988887777"),
                CustomerId.of("cust-020"),
                DebitCardBinding.of(SavingsAccountId.of("sav-020"), FxAccountId.of("fx-020")));

        primaryDebit.block();

        assertThrows(
                BusinessRuleViolation.class,
                () -> SupplementaryCardPolicy.ensurePrimaryDebitCardCanIssue(primaryDebit)
        );
    }

    /**
     * @brief 验证附属信用卡必须与主卡共用同一信用卡账户；
     *        Verify supplementary credit card must share same credit-card account as primary.
     */
    @Test
    void shouldRejectSupplementaryCreditCardWithDifferentAccount() {
        assertThrows(
                BusinessRuleViolation.class,
                () -> CardIssuingPolicy.ensureSupplementaryCreditAccountConsistency(
                        CreditCardAccountId.of("cc-acc-100"),
                        CreditCardAccountId.of("cc-acc-101"))
        );
    }

    /**
     * @brief 验证扣账卡绑定账户必须与持卡客户一致；
     *        Verify debit-card bound accounts must belong to holder customer.
     */
    @Test
    void shouldRejectDebitCardOwnershipMismatch() {
        assertThrows(
                BusinessRuleViolation.class,
                () -> CardIssuingPolicy.ensureDebitCardBindingOwnership(
                        CustomerId.of("cust-holder"),
                        CustomerId.of("cust-owner-a"),
                        CustomerId.of("cust-owner-b"))
        );
    }
}
