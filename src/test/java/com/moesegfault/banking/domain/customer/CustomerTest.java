package com.moesegfault.banking.domain.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import java.time.Instant;
import org.junit.jupiter.api.Test;

/**
 * @brief Customer 单元测试（Unit Test），覆盖客户注册、状态流转与值对象校验；
 *        Customer unit tests covering registration, status transition, and value-object validation.
 */
class CustomerTest {

    /**
     * @brief 验证注册客户默认状态与时间戳；
     *        Verify default status and timestamps when registering a customer.
     */
    @Test
    void shouldRegisterCustomerAsActive() {
        final Customer customer = Customer.register(
                CustomerId.of("cust-001"),
                IdentityDocument.of(IdentityDocumentType.PASSPORT, "p123456", "hk"),
                PhoneNumber.of("+852 9123 4567"),
                Address.of("Hong Kong Island"),
                Address.of("PO Box 100"),
                TaxProfile.of(false, (CrsInfo) null));

        assertEquals(CustomerStatus.ACTIVE, customer.customerStatus());
        assertNotNull(customer.createdAt());
        assertNotNull(customer.updatedAt());
        assertFalse(customer.updatedAt().isBefore(customer.createdAt()));
    }

    /**
     * @brief 验证客户可按规则冻结、解冻、关闭；
     *        Verify customer can be frozen, activated, and closed by rules.
     */
    @Test
    void shouldTransitionStatusByRules() {
        final Customer customer = registeredCustomer();

        customer.freeze();
        assertEquals(CustomerStatus.FROZEN, customer.customerStatus());

        customer.activate();
        assertEquals(CustomerStatus.ACTIVE, customer.customerStatus());

        customer.close();
        assertEquals(CustomerStatus.CLOSED, customer.customerStatus());
    }

    /**
     * @brief 验证关闭后更新资料会被拒绝；
     *        Verify updates are rejected when customer is closed.
     */
    @Test
    void shouldRejectUpdateWhenClosed() {
        final Customer customer = registeredCustomer();
        customer.close();

        assertThrows(BusinessRuleViolation.class, () -> customer.updateMobilePhone(PhoneNumber.of("+8613800000000")));
    }

    /**
     * @brief 验证身份证件会归一化到大写并校验长度；
     *        Verify identity document normalization and length validation.
     */
    @Test
    void shouldNormalizeIdentityDocumentFields() {
        final IdentityDocument document = IdentityDocument.of(IdentityDocumentType.HKID, " a123456(7) ", " hk ");

        assertEquals("A123456(7)", document.idNumber());
        assertEquals("HK", document.issuingRegion());
    }

    /**
     * @brief 验证手机号会归一化并做格式校验；
     *        Verify phone number normalization and format validation.
     */
    @Test
    void shouldNormalizeAndValidatePhoneNumber() {
        final PhoneNumber phoneNumber = PhoneNumber.of("+86 (138) 0000-0000");

        assertEquals("+8613800000000", phoneNumber.value());
        assertThrows(IllegalArgumentException.class, () -> PhoneNumber.of("abc"));
    }

    /**
     * @brief 验证重建客户时更新时间不能早于创建时间；
     *        Verify updated-at must not be earlier than created-at during restore.
     */
    @Test
    void shouldRejectUpdatedBeforeCreatedDuringRestore() {
        final Instant createdAt = Instant.parse("2026-01-10T10:00:00Z");
        final Instant updatedAt = Instant.parse("2026-01-09T10:00:00Z");

        assertThrows(
                BusinessRuleViolation.class,
                () -> Customer.restore(
                        CustomerId.of("cust-002"),
                        IdentityDocument.of(IdentityDocumentType.ID_CARD, "110101199901010011", "CN"),
                        PhoneNumber.of("+8613900000000"),
                        Address.of("Shanghai Pudong"),
                        Address.of("Shanghai Minhang"),
                        TaxProfile.of(true, CrsInfo.of("TIN: CN-123")),
                        CustomerStatus.ACTIVE,
                        createdAt,
                        updatedAt)
        );
    }

    /**
     * @brief 验证客户策略与状态一致；
     *        Verify customer policy is aligned with status constraints.
     */
    @Test
    void shouldEnforceCustomerPolicyOnStatus() {
        final Customer customer = registeredCustomer();
        CustomerPolicy.ensureEligibleForAccountOpening(customer);
        CustomerPolicy.ensureEligibleForCardIssuance(customer);

        customer.freeze();

        assertThrows(BusinessRuleViolation.class, () -> CustomerPolicy.ensureEligibleForAccountOpening(customer));
        assertTrue(CustomerPolicy.canBeClosed(customer));
    }

    /**
     * @brief 创建测试客户（Create Test Customer）；
     *        Create a test customer.
     *
     * @return 测试客户（Test customer）。
     */
    private static Customer registeredCustomer() {
        return Customer.register(
                CustomerId.of("cust-test"),
                IdentityDocument.of(IdentityDocumentType.PASSPORT, "e12345678", "CN"),
                PhoneNumber.of("+8613700000000"),
                Address.of("Shenzhen Nanshan"),
                Address.of("Shenzhen Futian"),
                TaxProfile.of(false, (CrsInfo) null));
    }
}
