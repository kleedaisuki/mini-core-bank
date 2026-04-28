package com.moesegfault.banking.domain.business;

import com.moesegfault.banking.domain.customer.CustomerId;
import java.util.List;
import java.util.Optional;

/**
 * @brief 业务仓储接口（Business Repository Interface），定义业务类型与业务交易聚合持久化契约；
 *        Business repository interface defining persistence contracts for business types and transactions.
 */
public interface BusinessRepository {

    /**
     * @brief 保存业务类型（Save Business Type）；
     *        Save business type entity.
     *
     * @param businessType 业务类型实体（Business type entity）。
     */
    void saveBusinessType(BusinessType businessType);

    /**
     * @brief 保存业务交易（Save Business Transaction）；
     *        Save business transaction entity.
     *
     * @param businessTransaction 业务交易实体（Business transaction entity）。
     */
    void saveTransaction(BusinessTransaction businessTransaction);

    /**
     * @brief 按业务类型码查询（Find Business Type by Code）；
     *        Find business type by type code.
     *
     * @param businessTypeCode 业务类型码（Business type code）。
     * @return 业务类型可选值（Optional business type）。
     */
    Optional<BusinessType> findBusinessTypeByCode(BusinessTypeCode businessTypeCode);

    /**
     * @brief 按交易 ID 查询（Find Transaction by ID）；
     *        Find transaction by transaction ID.
     *
     * @param transactionId 交易 ID（Transaction ID）。
     * @return 业务交易可选值（Optional business transaction）。
     */
    Optional<BusinessTransaction> findTransactionById(BusinessTransactionId transactionId);

    /**
     * @brief 按参考号查询（Find Transaction by Reference）；
     *        Find transaction by business reference.
     *
     * @param referenceNo 业务参考号（Business reference）。
     * @return 业务交易可选值（Optional business transaction）。
     */
    Optional<BusinessTransaction> findTransactionByReference(BusinessReference referenceNo);

    /**
     * @brief 判断参考号是否已存在（Exists Transaction by Reference）；
     *        Check whether transaction exists by business reference.
     *
     * @param referenceNo 业务参考号（Business reference）。
     * @return 已存在返回 true（true when exists）。
     */
    boolean existsTransactionByReference(BusinessReference referenceNo);

    /**
     * @brief 按发起客户查询交易列表（List Transactions by Initiator Customer）；
     *        List transactions by initiator customer ID.
     *
     * @param initiatorCustomerId 发起客户 ID（Initiator customer ID）。
     * @return 交易列表（Transaction list）。
     */
    List<BusinessTransaction> listTransactionsByCustomerId(CustomerId initiatorCustomerId);

    /**
     * @brief 按状态查询交易列表（List Transactions by Status）；
     *        List transactions by transaction status.
     *
     * @param transactionStatus 交易状态（Transaction status）。
     * @return 交易列表（Transaction list）。
     */
    List<BusinessTransaction> listTransactionsByStatus(BusinessTransactionStatus transactionStatus);

    /**
     * @brief 查询全部交易（Find All Transactions）；
     *        Find all business transactions.
     *
     * @return 业务交易列表（Business transaction list）。
     */
    List<BusinessTransaction> findAllTransactions();
}
