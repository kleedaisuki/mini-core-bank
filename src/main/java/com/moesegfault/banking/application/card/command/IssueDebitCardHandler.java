package com.moesegfault.banking.application.card.command;

import com.moesegfault.banking.application.card.CardApplicationException;
import com.moesegfault.banking.application.card.CardApplicationSupport;
import com.moesegfault.banking.application.card.result.CardKind;
import com.moesegfault.banking.application.card.result.IssueCardResult;
import com.moesegfault.banking.domain.account.AccountRepository;
import com.moesegfault.banking.domain.account.AccountType;
import com.moesegfault.banking.domain.account.FxAccount;
import com.moesegfault.banking.domain.account.SavingsAccount;
import com.moesegfault.banking.domain.card.CardId;
import com.moesegfault.banking.domain.card.CardIssuingPolicy;
import com.moesegfault.banking.domain.card.CardNumber;
import com.moesegfault.banking.domain.card.CardRepository;
import com.moesegfault.banking.domain.card.CustomerId;
import com.moesegfault.banking.domain.card.DebitCard;
import com.moesegfault.banking.domain.card.DebitCardBinding;
import com.moesegfault.banking.domain.customer.Customer;
import com.moesegfault.banking.domain.customer.CustomerRepository;
import com.moesegfault.banking.domain.shared.DomainEventPublisher;
import com.moesegfault.banking.infrastructure.id.IdGenerator;
import com.moesegfault.banking.infrastructure.persistence.transaction.DbTransactionManager;
import java.util.Objects;

/**
 * @brief 主借记卡发卡处理器（Issue Debit Card Handler），编排客户、账户与卡聚合完成发卡事务；
 *        Issue-debit-card handler orchestrating customer, account, and card aggregates in one transaction.
 */
public final class IssueDebitCardHandler {

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
     * @brief 构造主借记卡发卡处理器（Construct Issue Debit Card Handler）；
     *        Construct issue-debit-card handler.
     *
     * @param cardRepository     卡仓储接口（Card repository interface）。
     * @param accountRepository  账户仓储接口（Account repository interface）。
     * @param customerRepository 客户仓储接口（Customer repository interface）。
     * @param idGenerator        ID 生成器接口（ID generator interface）。
     * @param transactionManager 事务管理接口（Transaction manager interface）。
     * @param eventPublisher     领域事件发布接口（Domain event publisher interface）。
     */
    public IssueDebitCardHandler(
            final CardRepository cardRepository,
            final AccountRepository accountRepository,
            final CustomerRepository customerRepository,
            final IdGenerator idGenerator,
            final DbTransactionManager transactionManager,
            final DomainEventPublisher eventPublisher
    ) {
        this.cardRepository = Objects.requireNonNull(cardRepository, "cardRepository must not be null");
        this.accountRepository = Objects.requireNonNull(accountRepository, "accountRepository must not be null");
        this.customerRepository = Objects.requireNonNull(customerRepository, "customerRepository must not be null");
        this.idGenerator = Objects.requireNonNull(idGenerator, "idGenerator must not be null");
        this.transactionManager = Objects.requireNonNull(transactionManager, "transactionManager must not be null");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher must not be null");
    }

    /**
     * @brief 执行主借记卡发卡（Handle Debit Card Issuance）；
     *        Handle primary debit-card issuance.
     *
     * @param command 发卡命令（Issuance command）。
     * @return 发卡结果（Issuance result）。
     */
    public IssueCardResult handle(final IssueDebitCardCommand command) {
        final IssueDebitCardCommand normalizedCommand = Objects.requireNonNull(command, "command must not be null");
        return transactionManager.execute(() -> issueInTransaction(normalizedCommand));
    }

    /**
     * @brief 在事务中执行发卡编排（Execute Issuance Orchestration in Transaction）；
     *        Execute issuance orchestration inside one transaction.
     *
     * @param command 发卡命令（Issuance command）。
     * @return 发卡结果（Issuance result）。
     */
    private IssueCardResult issueInTransaction(final IssueDebitCardCommand command) {
        final com.moesegfault.banking.domain.customer.CustomerId holderCustomerId =
                com.moesegfault.banking.domain.customer.CustomerId.of(command.holderCustomerId());
        final Customer holder = customerRepository.findById(holderCustomerId)
                .orElseThrow(() -> new CardApplicationException(
                        "holder_customer_id not found in customer schema: " + command.holderCustomerId()));
        CardApplicationSupport.ensureEligibleCustomer(holder);

        final com.moesegfault.banking.domain.account.SavingsAccountId savingsAccountId =
                com.moesegfault.banking.domain.account.SavingsAccountId.of(command.savingsAccountId());
        final SavingsAccount savingsAccount = accountRepository.findSavingsAccountById(savingsAccountId)
                .orElseThrow(() -> new CardApplicationException(
                        "savings_account_id not found in savings_account schema: " + command.savingsAccountId()));
        CardApplicationSupport.ensureActiveAccount(
                savingsAccount.account(),
                AccountType.SAVINGS,
                "savings_account");

        final com.moesegfault.banking.domain.account.FxAccountId fxAccountId =
                com.moesegfault.banking.domain.account.FxAccountId.of(command.fxAccountId());
        final FxAccount fxAccount = accountRepository.findFxAccountById(fxAccountId)
                .orElseThrow(() -> new CardApplicationException(
                        "fx_account_id not found in fx_account schema: " + command.fxAccountId()));
        CardApplicationSupport.ensureActiveAccount(
                fxAccount.account(),
                AccountType.FX,
                "fx_account");

        if (!fxAccount.linkedSavingsAccountId().sameValueAs(savingsAccount.savingsAccountId())) {
            throw new CardApplicationException(
                    "fx_account.linked_savings_account_id must match bound savings_account_id");
        }

        CardIssuingPolicy.ensureDebitCardBindingOwnership(
                CustomerId.of(holder.customerId().value()),
                CustomerId.of(savingsAccount.account().customerId().value()),
                CustomerId.of(fxAccount.account().customerId().value()));

        final CardNumber cardNumber = CardNumber.of(command.cardNo());
        CardApplicationSupport.ensureUniqueCardNumber(cardRepository, cardNumber);

        final DebitCard debitCard = DebitCard.issue(
                CardId.of(idGenerator.nextId()),
                cardNumber,
                CustomerId.of(holder.customerId().value()),
                DebitCardBinding.of(
                        com.moesegfault.banking.domain.card.SavingsAccountId.of(savingsAccount.savingsAccountId().value()),
                        com.moesegfault.banking.domain.card.FxAccountId.of(fxAccount.fxAccountId().value())));
        cardRepository.saveDebitCard(debitCard);
        eventPublisher.publish(debitCard.issuedEvent());

        return new IssueCardResult(
                debitCard.cardId().value(),
                debitCard.cardNumber().masked(),
                CardKind.DEBIT,
                debitCard.cardStatus().name(),
                debitCard.holderCustomerId().value(),
                debitCard.issuedAt(),
                null);
    }
}
