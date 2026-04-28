package com.moesegfault.banking.application.card.command;

import com.moesegfault.banking.application.card.CardApplicationException;
import com.moesegfault.banking.application.card.CardApplicationSupport;
import com.moesegfault.banking.application.card.result.CardKind;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.domain.account.Account;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.AccountType;
import com.moesegfault.banking.domain.card.CardId;
import com.moesegfault.banking.domain.card.CardIssuingPolicy;
import com.moesegfault.banking.domain.card.CardNumber;
import com.moesegfault.banking.domain.card.CardRepository;
import com.moesegfault.banking.domain.card.CreditCard;
import com.moesegfault.banking.domain.card.CreditCardAccountId;
import com.moesegfault.banking.domain.card.CustomerId;
import com.moesegfault.banking.domain.card.SupplementaryCardPolicy;
import com.moesegfault.banking.domain.credit.CreditRepository;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;

/**
 * @brief 信用附属卡发卡处理器（Issue Supplementary Credit Card Handler），编排主卡一致性与附属卡发行；
 *        Issue-supplementary-credit-card handler orchestrating primary-card consistency and issuance.
 */
public final class IssueSupplementaryCreditCardHandler {

    /**
     * @brief 卡仓储接口（Card Repository Interface）；
     *        Card repository interface.
     */
    private final CardRepository cardRepository;

    /**
     * @brief 账户仓储接口（Account Repository Interface）；
     *        Account repository interface.
     */
    private final AccountRepository accountRepository;

    /**
     * @brief 客户仓储接口（Customer Repository Interface）；
     *        Customer repository interface.
     */
    private final CustomerRepository customerRepository;

    /**
     * @brief 信用仓储接口（Credit Repository Interface）；
     *        Credit repository interface.
     */
    private final CreditRepository creditRepository;

    /**
     * @brief ID 生成器接口（ID Generator Interface）；
     *        ID generator interface.
     */
    private final IdGenerator idGenerator;

    /**
     * @brief 事务管理接口（Transaction Manager Interface）；
     *        Transaction manager interface.
     */
    private final DbTransactionManager transactionManager;

    /**
     * @brief 领域事件发布接口（Domain Event Publisher Interface）；
     *        Domain event publisher interface.
     */
    private final DomainEventPublisher eventPublisher;

    /**
     * @brief 构造信用附属卡发卡处理器（Construct Issue Supplementary Credit Card Handler）；
     *        Construct issue-supplementary-credit-card handler.
     *
     * @param cardRepository     卡仓储接口（Card repository interface）。
     * @param accountRepository  账户仓储接口（Account repository interface）。
     * @param customerRepository 客户仓储接口（Customer repository interface）。
     * @param creditRepository   信用仓储接口（Credit repository interface）。
     * @param idGenerator        ID 生成器接口（ID generator interface）。
     * @param transactionManager 事务管理接口（Transaction manager interface）。
     * @param eventPublisher     领域事件发布接口（Domain event publisher interface）。
     */
    public IssueSupplementaryCreditCardHandler(
            final CardRepository cardRepository,
            final AccountRepository accountRepository,
            final CustomerRepository customerRepository,
            final CreditRepository creditRepository,
            final IdGenerator idGenerator,
            final DbTransactionManager transactionManager,
            final DomainEventPublisher eventPublisher
    ) {
        this.cardRepository = Objects.requireNonNull(cardRepository, "cardRepository must not be null");
        this.accountRepository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
        this.customerRepository = Objects.requireNonNull(customerRepository, "customerRepository must not be null");
        this.creditRepository = Objects.requireNonNull(creditRepository, "creditRepository must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager must not be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher must not be null");
    }

    /**
     * @brief 执行信用附属卡发卡（Handle Supplementary Credit Card Issuance）；
     *        Handle supplementary credit-card issuance.
     *
     * @param command 发卡命令（Issuance command）。
     * @return 发卡结果（Issuance result）。
     */
    public IssueCardResult handle(final IssueSupplementaryCreditCardCommand command) {
        final IssueSupplementaryCreditCardCommand normalizedCommand =
                Objects.requireNonNull(command, "command must not be null");
        return transactionManager.execute(() -> issueInTransaction(normalizedCommand));
    }

    /**
     * @brief 在事务中执行发卡编排（Execute Issuance Orchestration in Transaction）；
     *        Execute issuance orchestration inside one transaction.
     *
     * @param command 发卡命令（Issuance command）。
     * @return 发卡结果（Issuance result）。
     */
    private IssueCardResult issueInTransaction(final IssueSupplementaryCreditCardCommand command) {
        final com.moesegfault.banking.domain.customer.CustomerId holderCustomerId =
                com.moesegfault.banking.domain.customer.CustomerId.of(command.holderCustomerId());
        final Customer holder = customerRepository.findById(holderCustomerId)
                .orElseThrow(() -> new CardApplicationException(
                        "holder_customer_id not found in customer schema: " + command.holderCustomerId()));
        CardApplicationSupport.ensureEligibleCustomer(holder);

        final CardId primaryCreditCardId = CardId.of(command.primaryCreditCardId());
        final CreditCard primaryCreditCard = cardRepository.findCreditCardById(primaryCreditCardId)
                .orElseThrow(() -> new CardApplicationException(
                        "primary_credit_card_id not found in credit_card schema: " + command.primaryCreditCardId()));
        SupplementaryCardPolicy.ensurePrimaryCreditCardCanIssue(primaryCreditCard);

        final com.moesegfault.banking.domain.account.AccountId accountId =
                com.moesegfault.banking.domain.account.AccountId.of(command.creditCardAccountId());
        final Account account = accountRepository.findAccountById(accountId)
                .orElseThrow(() -> new CardApplicationException(
                        "credit_card_account_id not found in account schema: " + command.creditCardAccountId()));
        CardApplicationSupport.ensureActiveAccount(account, AccountType.CREDIT_CARD, "credit_card_account");

        CardApplicationSupport.ensurePrimaryCardOwnershipConsistency(
                primaryCreditCard.holderCustomerId().value(),
                account.customerId().value());

        final CreditCardAccountId supplementaryAccountId = CreditCardAccountId.of(command.creditCardAccountId());
        CardIssuingPolicy.ensureSupplementaryCreditAccountConsistency(
                primaryCreditCard.creditCardAccountId(),
                supplementaryAccountId);

        final com.moesegfault.banking.domain.credit.CreditCardAccountId creditAccountIdInCreditDomain =
                com.moesegfault.banking.domain.credit.CreditCardAccountId.of(command.creditCardAccountId());
        creditRepository.findCreditCardAccountById(creditAccountIdInCreditDomain)
                .orElseThrow(() -> new CardApplicationException(
                        "credit_card_account_id not found in credit_card_account schema: " + command.creditCardAccountId()));

        final CardNumber cardNumber = CardNumber.of(command.cardNo());
        CardApplicationSupport.ensureUniqueCardNumber(cardRepository, cardNumber);

        final CreditCard supplementaryCreditCard = CreditCard.issueSupplementary(
                CardId.of(idGenerator.nextId()),
                cardNumber,
                CustomerId.of(holder.customerId().value()),
                supplementaryAccountId,
                primaryCreditCardId);
        cardRepository.saveCreditCard(supplementaryCreditCard);
        eventPublisher.publish(supplementaryCreditCard.issuedEvent());
        eventPublisher.publish(supplementaryCreditCard.supplementaryIssuedEvent());

        return new IssueCardResult(
                supplementaryCreditCard.creditCardId().value(),
                supplementaryCreditCard.cardNumber().masked(),
                CardKind.SUPPLEMENTARY_CREDIT,
                supplementaryCreditCard.cardStatus().name(),
                supplementaryCreditCard.holderCustomerId().value(),
                supplementaryCreditCard.issuedAt(),
                primaryCreditCardId.value());
    }
}
