package com.moesegfault.banking.domain.credit;

import com.moesegfault.banking.domain.account.CreditCardAccountId;
import com.moesegfault.banking.domain.shared.DateRange;
import java.util.List;
import java.util.Optional;

/**
 * @brief 信用领域仓储接口（Credit Repository Interface），定义信用账户与账单持久化契约；
 *        Credit repository interface defining persistence contracts for credit accounts and statements.
 */
public interface CreditRepository {

    /**
     * @brief 保存信用卡账户（Save Credit Card Account）；
     *        Save credit-card-account entity.
     *
     * @param creditCardAccount 信用卡账户实体（Credit-card-account entity）。
     */
    void saveCreditCardAccount(CreditCardAccount creditCardAccount);

    /**
     * @brief 保存信用卡账单（Save Credit Card Statement）；
     *        Save credit-card-statement entity.
     *
     * @param creditCardStatement 信用卡账单实体（Credit-card-statement entity）。
     */
    void saveCreditCardStatement(CreditCardStatement creditCardStatement);

    /**
     * @brief 按账户 ID 查询信用卡账户（Find Credit Card Account by ID）；
     *        Find credit-card account by account ID.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @return 信用卡账户可选值（Optional credit-card account）。
     */
    Optional<CreditCardAccount> findCreditCardAccountById(CreditCardAccountId creditCardAccountId);

    /**
     * @brief 按账单 ID 查询账单（Find Statement by ID）；
     *        Find statement by statement ID.
     *
     * @param statementId 账单 ID（Statement ID）。
     * @return 账单可选值（Optional statement）。
     */
    Optional<CreditCardStatement> findStatementById(StatementId statementId);

    /**
     * @brief 按账期查询账单（Find Statement by Period）；
     *        Find statement by account and statement period.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @param statementPeriod     账期范围（Statement period）。
     * @return 账单可选值（Optional statement）。
     */
    Optional<CreditCardStatement> findStatementByPeriod(
            CreditCardAccountId creditCardAccountId,
            DateRange statementPeriod);

    /**
     * @brief 查询账户下可还款账单（List Repayable Statements by Account）；
     *        List repayable statements by account ID.
     *
     * @param creditCardAccountId 信用卡账户 ID（Credit-card-account ID）。
     * @return 账单列表（Statement list）。
     */
    List<CreditCardStatement> listRepayableStatementsByAccountId(CreditCardAccountId creditCardAccountId);
}
