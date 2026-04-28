package com.moesegfault.banking.domain.ledger;

import com.moesegfault.banking.domain.shared.CurrencyCode;
import java.util.List;
import java.util.Optional;

/**
 * @brief 账务仓储接口（Ledger Repository Interface），定义余额、分录与批次持久化契约；
 *        Ledger repository interface defining persistence contracts for balances, entries, and batches.
 */
public interface LedgerRepository {

    /**
     * @brief 保存余额（Save Balance）；
     *        Save account balance entity.
     *
     * @param balance 余额实体（Balance entity）。
     */
    void saveBalance(Balance balance);

    /**
     * @brief 保存分录（Save Ledger Entry）；
     *        Save ledger-entry entity.
     *
     * @param ledgerEntry 分录实体（Ledger-entry entity）。
     */
    void saveEntry(LedgerEntry ledgerEntry);

    /**
     * @brief 批量保存分录（Save Ledger Entries in Batch）；
     *        Save ledger entries in batch.
     *
     * @param ledgerEntries 分录列表（Ledger-entry list）。
     */
    void saveEntries(List<LedgerEntry> ledgerEntries);

    /**
     * @brief 保存入账批次（Save Posting Batch）；
     *        Save posting-batch entity.
     *
     * @param postingBatch 批次实体（Posting-batch entity）。
     */
    void savePostingBatch(PostingBatch postingBatch);

    /**
     * @brief 按批次 ID 查询入账批次（Find Posting Batch by ID）；
     *        Find posting batch by batch ID.
     *
     * @param postingBatchId 批次 ID（Posting-batch ID）。
     * @return 批次可选值（Optional posting batch）。
     */
    Optional<PostingBatch> findPostingBatchById(PostingBatchId postingBatchId);

    /**
     * @brief 按交易 ID 查询入账批次（Find Posting Batch by Transaction ID）；
     *        Find posting batch by transaction ID.
     *
     * @param transactionId 交易 ID（Transaction ID）。
     * @return 批次可选值（Optional posting batch）。
     */
    Optional<PostingBatch> findPostingBatchByTransactionId(String transactionId);

    /**
     * @brief 按幂等键查询入账批次（Find Posting Batch by Idempotency Key）；
     *        Find posting batch by idempotency key.
     *
     * @param idempotencyKey 幂等键（Idempotency key）。
     * @return 批次可选值（Optional posting batch）。
     */
    Optional<PostingBatch> findPostingBatchByIdempotencyKey(String idempotencyKey);

    /**
     * @brief 按账户+币种查询余额（Find Balance by Account and Currency）；
     *        Find balance by account ID and currency code.
     *
     * @param accountId    账户 ID（Account ID）。
     * @param currencyCode 币种代码（Currency code）。
     * @return 余额可选值（Optional balance）。
     */
    Optional<Balance> findBalance(String accountId, CurrencyCode currencyCode);

    /**
     * @brief 查询账户全部币种余额（List Balances by Account ID）；
     *        List all currency balances for an account.
     *
     * @param accountId 账户 ID（Account ID）。
     * @return 余额列表（Balance list）。
     */
    List<Balance> listBalancesByAccountId(String accountId);

    /**
     * @brief 按交易 ID 查询分录（List Entries by Transaction ID）；
     *        List entries by transaction ID.
     *
     * @param transactionId 交易 ID（Transaction ID）。
     * @return 分录列表（Entry list）。
     */
    List<LedgerEntry> listEntriesByTransactionId(String transactionId);

    /**
     * @brief 查询账户最近分录（List Recent Entries by Account ID）；
     *        List recent entries by account ID.
     *
     * @param accountId 账户 ID（Account ID）。
     * @param limit     返回条数上限（Maximum number of entries）。
     * @return 分录列表（Entry list）。
     */
    List<LedgerEntry> listRecentEntriesByAccountId(String accountId, int limit);
}
