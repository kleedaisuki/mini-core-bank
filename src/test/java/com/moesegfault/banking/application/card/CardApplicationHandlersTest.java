package com.moesegfault.banking.application.card;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.moesegfault.banking.application.card.command.IssueCreditCardCommand;
import com.moesegfault.banking.application.card.command.IssueCreditCardHandler;
import com.moesegfault.banking.application.card.command.IssueDebitCardCommand;
import com.moesegfault.banking.application.card.command.IssueDebitCardHandler;
import com.moesegfault.banking.application.card.command.IssueSupplementaryCreditCardCommand;
import com.moesegfault.banking.application.card.command.IssueSupplementaryCreditCardHandler;
import com.moesegfault.banking.application.card.query.FindCardHandler;
import com.moesegfault.banking.application.card.query.FindCardQuery;
import com.moesegfault.banking.application.card.result.CardKind;
import com.moesegfault.banking.application.card.result.CardResult;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountNumber;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.AccountType;
import com.moesegfault.banking.domain.account.FxAccount;
import com.moesegfault.banking.domain.account.FxAccountId;
import com.moesegfault.banking.domain.account.SavingsAccount;
import com.moesegfault.banking.domain.account.SavingsAccountId;
import com.moesegfault.banking.domain.card.CardId;
import com.moesegfault.banking.domain.card.CardNumber;
import com.moesegfault.banking.domain.card.CardRepository;
import com.moesegfault.banking.domain.card.CreditCard;
import com.moesegfault.banking.domain.card.CreditCardAccountId;
import com.moesegfault.banking.domain.card.CustomerId;
import com.moesegfault.banking.domain.card.DebitCard;
import com.moesegfault.banking.domain.card.DebitCardBinding;
import com.moesegfault.banking.domain.card.SupplementaryDebitCard;
import com.moesegfault.banking.domain.credit.BillingCycle;
import com.moesegfault.banking.domain.credit.CreditCardAccount;
import com.moesegfault.banking.domain.credit.CreditRepository;
import com.moesegfault.banking.domain.customer.Address;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.customer.IdentityDocument;
import com.moesegfault.banking.domain.customer.IdentityDocumentType;
import com.moesegfault.banking.domain.customer.PhoneNumber;
import com.moesegfault.banking.domain.customer.TaxProfile;
import com.moesegfault.banking.domain.shared.BusinessRuleViolation;
import com.moesegfault.banking.domain.shared.CurrencyCode;
import com.moesegfault.banking.domain.shared.DomainEvent;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.domain.shared.Money;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

/**
 * @brief 卡应用层处理器测试（Card Application Handler Test），覆盖发卡编排主路径与关键约束；
 *        Card application handler tests covering issuance orchestration happy paths and critical constraints.
 */
class CardApplicationHandlersTest {

    /**
     * @brief 测试主借记卡发卡成功路径；
     *        Test successful path for primary debit-card issuance.
     */
    @Test
    void shouldIssueDebitCardWhenCustomerAndAccountsAreEligible() {
        final CardRepository cardRepository = Mockito.mock(CardRepository.class);
        final AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        final CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
        final IdGenerator idGenerator = () -> "debit-card-001";
        final DbTransactionManager transactionManager = new ImmediateTransactionManager();
        final DomainEventPublisher eventPublisher = Mockito.mock(DomainEventPublisher.class);

        final IssueDebitCardHandler handler = new IssueDebitCardHandler(
                cardRepository,
                accountRepository,
                customerRepository,
                idGenerator,
                transactionManager,
                eventPublisher);

        final Customer customer = activeCustomer("cust-001");
        final SavingsAccount savingsAccount = SavingsAccount.open(
                SavingsAccountId.of("sav-001"),
                com.moesegfault.banking.domain.account.CustomerId.of("cust-001"),
                AccountNumber.of("SAV-001"));
        final FxAccount fxAccount = FxAccount.open(
                FxAccountId.of("fx-001"),
                com.moesegfault.banking.domain.account.CustomerId.of("cust-001"),
                AccountNumber.of("FX-001"),
                SavingsAccountId.of("sav-001"));

        when(customerRepository.findById(com.moesegfault.banking.domain.customer.CustomerId.of("cust-001")))
                .thenReturn(Optional.of(customer));
        when(accountRepository.findSavingsAccountById(SavingsAccountId.of("sav-001")))
                .thenReturn(Optional.of(savingsAccount));
        when(accountRepository.findFxAccountById(FxAccountId.of("fx-001")))
                .thenReturn(Optional.of(fxAccount));
        when(cardRepository.existsAnyByCardNumber(CardNumber.of("6222333344445555")))
                .thenReturn(false);

        final IssueCardResult result = handler.handle(new IssueDebitCardCommand(
                "cust-001",
                "sav-001",
                "fx-001",
                "6222333344445555"));

        assertEquals("debit-card-001", result.cardId());
        assertEquals(CardKind.DEBIT, result.cardKind());
        assertEquals("cust-001", result.holderCustomerId());
        verify(cardRepository, times(1)).saveDebitCard(any(DebitCard.class));
        verify(eventPublisher, times(1)).publish(any(DomainEvent.class));
    }

    /**
     * @brief 测试主借记卡发卡时 FX 绑定储蓄账户不匹配会失败；
     *        Test debit-card issuance fails when FX linked savings account mismatches command binding.
     */
    @Test
    void shouldRejectDebitCardIssuanceWhenFxLinkedSavingsMismatch() {
        final CardRepository cardRepository = Mockito.mock(CardRepository.class);
        final AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        final CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
        final IdGenerator idGenerator = () -> "debit-card-002";
        final DbTransactionManager transactionManager = new ImmediateTransactionManager();
        final DomainEventPublisher eventPublisher = Mockito.mock(DomainEventPublisher.class);

        final IssueDebitCardHandler handler = new IssueDebitCardHandler(
                cardRepository,
                accountRepository,
                customerRepository,
                idGenerator,
                transactionManager,
                eventPublisher);

        final Customer customer = activeCustomer("cust-001");
        final SavingsAccount savingsAccount = SavingsAccount.open(
                SavingsAccountId.of("sav-001"),
                com.moesegfault.banking.domain.account.CustomerId.of("cust-001"),
                AccountNumber.of("SAV-001"));
        final FxAccount fxAccount = FxAccount.open(
                FxAccountId.of("fx-001"),
                com.moesegfault.banking.domain.account.CustomerId.of("cust-001"),
                AccountNumber.of("FX-001"),
                SavingsAccountId.of("sav-999"));

        when(customerRepository.findById(com.moesegfault.banking.domain.customer.CustomerId.of("cust-001")))
                .thenReturn(Optional.of(customer));
        when(accountRepository.findSavingsAccountById(SavingsAccountId.of("sav-001")))
                .thenReturn(Optional.of(savingsAccount));
        when(accountRepository.findFxAccountById(FxAccountId.of("fx-001")))
                .thenReturn(Optional.of(fxAccount));

        assertThrows(CardApplicationException.class, () -> handler.handle(new IssueDebitCardCommand(
                "cust-001",
                "sav-001",
                "fx-001",
                "6222333344445555")));
        verify(cardRepository, never()).saveDebitCard(any(DebitCard.class));
    }

    /**
     * @brief 测试主信用卡发卡成功路径；
     *        Test successful path for primary credit-card issuance.
     */
    @Test
    void shouldIssuePrimaryCreditCardWhenCreditAccountIsEligible() {
        final CardRepository cardRepository = Mockito.mock(CardRepository.class);
        final AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        final CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
        final CreditRepository creditRepository = Mockito.mock(CreditRepository.class);
        final IdGenerator idGenerator = () -> "credit-card-001";
        final DbTransactionManager transactionManager = new ImmediateTransactionManager();
        final DomainEventPublisher eventPublisher = Mockito.mock(DomainEventPublisher.class);

        final IssueCreditCardHandler handler = new IssueCreditCardHandler(
                cardRepository,
                accountRepository,
                customerRepository,
                creditRepository,
                idGenerator,
                transactionManager,
                eventPublisher);

        final Customer customer = activeCustomer("cust-010");
        final Account creditAccount = Account.open(
                com.moesegfault.banking.domain.account.AccountId.of("cc-acc-001"),
                com.moesegfault.banking.domain.account.CustomerId.of("cust-010"),
                AccountNumber.of("CC-ACC-001"),
                AccountType.CREDIT_CARD);
        final CreditCardAccount creditCardAccount = sampleCreditCardAccount("cc-acc-001");

        when(customerRepository.findById(com.moesegfault.banking.domain.customer.CustomerId.of("cust-010")))
                .thenReturn(Optional.of(customer));
        when(accountRepository.findAccountById(com.moesegfault.banking.domain.account.AccountId.of("cc-acc-001")))
                .thenReturn(Optional.of(creditAccount));
        when(creditRepository.findCreditCardAccountById(com.moesegfault.banking.domain.credit.CreditCardAccountId.of(
                "cc-acc-001"))).thenReturn(Optional.of(creditCardAccount));
        when(cardRepository.findCreditCardsByAccountId(CreditCardAccountId.of("cc-acc-001")))
                .thenReturn(List.of());
        when(cardRepository.existsAnyByCardNumber(CardNumber.of("4111222233334444")))
                .thenReturn(false);

        final IssueCardResult result = handler.handle(new IssueCreditCardCommand(
                "cust-010",
                "cc-acc-001",
                "4111222233334444"));

        assertEquals("credit-card-001", result.cardId());
        assertEquals(CardKind.PRIMARY_CREDIT, result.cardKind());
        verify(cardRepository, times(1)).saveCreditCard(any(CreditCard.class));
        verify(eventPublisher, times(1)).publish(any(DomainEvent.class));
    }

    /**
     * @brief 测试信用附属卡发卡时账户不一致会失败；
     *        Test supplementary credit-card issuance fails when account IDs are inconsistent.
     */
    @Test
    void shouldRejectSupplementaryCreditCardIssuanceWhenAccountMismatchesPrimary() {
        final CardRepository cardRepository = Mockito.mock(CardRepository.class);
        final AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        final CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
        final CreditRepository creditRepository = Mockito.mock(CreditRepository.class);
        final IdGenerator idGenerator = () -> "credit-card-002";
        final DbTransactionManager transactionManager = new ImmediateTransactionManager();
        final DomainEventPublisher eventPublisher = Mockito.mock(DomainEventPublisher.class);

        final IssueSupplementaryCreditCardHandler handler = new IssueSupplementaryCreditCardHandler(
                cardRepository,
                accountRepository,
                customerRepository,
                creditRepository,
                idGenerator,
                transactionManager,
                eventPublisher);

        final Customer holder = activeCustomer("cust-020");
        final CreditCard primaryCreditCard = CreditCard.issuePrimary(
                CardId.of("primary-credit-001"),
                CardNumber.of("4111222233335555"),
                CustomerId.of("cust-010"),
                CreditCardAccountId.of("cc-acc-001"));
        final Account commandAccount = Account.open(
                com.moesegfault.banking.domain.account.AccountId.of("cc-acc-999"),
                com.moesegfault.banking.domain.account.CustomerId.of("cust-010"),
                AccountNumber.of("CC-ACC-999"),
                AccountType.CREDIT_CARD);

        when(customerRepository.findById(com.moesegfault.banking.domain.customer.CustomerId.of("cust-020")))
                .thenReturn(Optional.of(holder));
        when(cardRepository.findCreditCardById(CardId.of("primary-credit-001")))
                .thenReturn(Optional.of(primaryCreditCard));
        when(accountRepository.findAccountById(com.moesegfault.banking.domain.account.AccountId.of("cc-acc-999")))
                .thenReturn(Optional.of(commandAccount));

        assertThrows(BusinessRuleViolation.class, () -> handler.handle(new IssueSupplementaryCreditCardCommand(
                "cust-020",
                "primary-credit-001",
                "cc-acc-999",
                "4111222233336666")));
        verify(cardRepository, never()).saveCreditCard(any(CreditCard.class));
    }

    /**
     * @brief 测试卡片查询会按三张卡表顺序返回统一结果；
     *        Test find-card query returns unified result in expected table lookup order.
     */
    @Test
    void shouldFindSupplementaryDebitCardAsUnifiedResult() {
        final CardRepository cardRepository = Mockito.mock(CardRepository.class);
        final FindCardHandler handler = new FindCardHandler(cardRepository);

        final SupplementaryDebitCard supplementaryDebitCard = SupplementaryDebitCard.issue(
                CardId.of("sup-debit-001"),
                CardNumber.of("6222999988887777"),
                CustomerId.of("cust-030"),
                CardId.of("debit-001"));

        when(cardRepository.findDebitCardById(CardId.of("sup-debit-001")))
                .thenReturn(Optional.empty());
        when(cardRepository.findSupplementaryDebitCardById(CardId.of("sup-debit-001")))
                .thenReturn(Optional.of(supplementaryDebitCard));

        final CardResult cardResult = handler.handle(new FindCardQuery("sup-debit-001"));

        assertEquals(CardKind.SUPPLEMENTARY_DEBIT, cardResult.cardKind());
        assertEquals("sup-debit-001", cardResult.cardId());
        assertEquals("debit-001", cardResult.primaryCardIdOrNull());
    }

    /**
     * @brief 测试主信用卡发卡会阻止重复主卡；
     *        Test primary credit-card issuance rejects duplicate primary card in same account.
     */
    @Test
    void shouldRejectPrimaryCreditCardWhenPrimaryAlreadyExistsInAccount() {
        final CardRepository cardRepository = Mockito.mock(CardRepository.class);
        final AccountRepository accountRepository = Mockito.mock(AccountRepository.class);
        final CustomerRepository customerRepository = Mockito.mock(CustomerRepository.class);
        final CreditRepository creditRepository = Mockito.mock(CreditRepository.class);
        final IdGenerator idGenerator = () -> "credit-card-003";
        final DbTransactionManager transactionManager = new ImmediateTransactionManager();
        final DomainEventPublisher eventPublisher = Mockito.mock(DomainEventPublisher.class);

        final IssueCreditCardHandler handler = new IssueCreditCardHandler(
                cardRepository,
                accountRepository,
                customerRepository,
                creditRepository,
                idGenerator,
                transactionManager,
                eventPublisher);

        final Customer customer = activeCustomer("cust-010");
        final Account creditAccount = Account.open(
                com.moesegfault.banking.domain.account.AccountId.of("cc-acc-001"),
                com.moesegfault.banking.domain.account.CustomerId.of("cust-010"),
                AccountNumber.of("CC-ACC-001"),
                AccountType.CREDIT_CARD);
        final CreditCardAccount creditCardAccount = sampleCreditCardAccount("cc-acc-001");
        final CreditCard existingPrimary = CreditCard.issuePrimary(
                CardId.of("existing-primary"),
                CardNumber.of("4111222233337777"),
                CustomerId.of("cust-010"),
                CreditCardAccountId.of("cc-acc-001"));

        when(customerRepository.findById(com.moesegfault.banking.domain.customer.CustomerId.of("cust-010")))
                .thenReturn(Optional.of(customer));
        when(accountRepository.findAccountById(com.moesegfault.banking.domain.account.AccountId.of("cc-acc-001")))
                .thenReturn(Optional.of(creditAccount));
        when(creditRepository.findCreditCardAccountById(com.moesegfault.banking.domain.credit.CreditCardAccountId.of(
                "cc-acc-001"))).thenReturn(Optional.of(creditCardAccount));
        when(cardRepository.findCreditCardsByAccountId(CreditCardAccountId.of("cc-acc-001")))
                .thenReturn(List.of(existingPrimary));

        final CardApplicationException exception = assertThrows(
                CardApplicationException.class,
                () -> handler.handle(new IssueCreditCardCommand("cust-010", "cc-acc-001", "4111222233334444")));
        assertInstanceOf(CardApplicationException.class, exception);
        verify(cardRepository, never()).saveCreditCard(any(CreditCard.class));
    }

    /**
     * @brief 创建激活客户测试样本（Create Active Customer Fixture）；
     *        Create an active customer fixture.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @return 客户实体（Customer entity）。
     */
    private static Customer activeCustomer(final String customerId) {
        return Customer.register(
                com.moesegfault.banking.domain.customer.CustomerId.of(customerId),
                IdentityDocument.of(IdentityDocumentType.ID_CARD, "A123456789", "CN"),
                PhoneNumber.of("+8613711111111"),
                Address.of("Shanghai Pudong"),
                Address.of("Shanghai Pudong"),
                TaxProfile.of(false, (String) null));
    }

    /**
     * @brief 创建信用卡账户测试样本（Create Credit Card Account Fixture）；
     *        Create a credit-card-account fixture.
     *
     * @param accountId 账户 ID（Account ID）。
     * @return 信用卡账户实体（Credit card account entity）。
     */
    private static CreditCardAccount sampleCreditCardAccount(final String accountId) {
        final CurrencyCode usd = CurrencyCode.of("USD");
        return CreditCardAccount.open(
                com.moesegfault.banking.domain.credit.CreditCardAccountId.of(accountId),
                Money.of(usd, new BigDecimal("10000.0000")),
                BillingCycle.of(5, 25),
                com.moesegfault.banking.domain.credit.InterestRate.ofDecimal(new BigDecimal("0.010000")),
                Money.of(usd, new BigDecimal("2000.0000")),
                usd);
    }

    /**
     * @brief 立即执行事务管理器（Immediate Transaction Manager），用于单测简化事务边界；
     *        Immediate transaction manager for simplifying transaction boundary in tests.
     */
    private static final class ImmediateTransactionManager implements DbTransactionManager {

        /**
         * @brief 在当前线程直接执行（Execute Inline in Current Thread）；
         *        Execute supplier inline on current thread.
         *
         * @param action 事务动作（Transactional action）。
         * @param <T>    返回类型（Result type）。
         * @return 执行结果（Execution result）。
         */
        @Override
        public <T> T execute(final Supplier<T> action) {
            return action.get();
        }
    }
}
