package com.moesegfault.banking.infrastructure.persistence.sql;

/**
 * @brief 业务 SQL 常量（Business SQL Constants）；
 *        Centralized SQL constants for business persistence.
 */
public final class BusinessSql {

        /**
         * @brief 业务类型 upsert SQL（Business Type Upsert SQL）；
         *        SQL for insert-or-update business type record.
         */
        public static final String UPSERT_TYPE = """
                        INSERT INTO business_type (
                            business_type_code,
                            business_category,
                            business_name,
                            description,
                            is_financial,
                            is_reversible,
                            status
                        ) VALUES (?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT (business_type_code) DO UPDATE SET
                            business_category = EXCLUDED.business_category,
                            business_name = EXCLUDED.business_name,
                            description = EXCLUDED.description,
                            is_financial = EXCLUDED.is_financial,
                            is_reversible = EXCLUDED.is_reversible,
                            status = EXCLUDED.status
                        """;

        /**
         * @brief 业务交易 upsert SQL（Business Transaction Upsert SQL）；
         *        SQL for insert-or-update business transaction record.
         */
        public static final String UPSERT_TRANSACTION = """
                        INSERT INTO business_transaction (
                            transaction_id,
                            business_type_code,
                            initiator_customer_id,
                            operator_id,
                            channel,
                            transaction_status,
                            requested_at,
                            completed_at,
                            reference_no,
                            remarks
                        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        ON CONFLICT (transaction_id) DO UPDATE SET
                            business_type_code = EXCLUDED.business_type_code,
                            initiator_customer_id = EXCLUDED.initiator_customer_id,
                            operator_id = EXCLUDED.operator_id,
                            channel = EXCLUDED.channel,
                            transaction_status = EXCLUDED.transaction_status,
                            requested_at = EXCLUDED.requested_at,
                            completed_at = EXCLUDED.completed_at,
                            reference_no = EXCLUDED.reference_no,
                            remarks = EXCLUDED.remarks
                        """;

        /**
         * @brief 交易参考号存在性检查 SQL（Exists Transaction by Reference SQL）；
         *        SQL for checking transaction existence by reference number.
         */
        public static final String EXISTS_TRANSACTION_BY_REFERENCE = "SELECT EXISTS (SELECT 1 FROM business_transaction WHERE reference_no = ?)";

        /**
         * @brief 按客户列出交易 SQL（List Transactions by Customer ID SQL）；
         *        SQL for listing transactions by initiator customer.
         */
        public static final String LIST_TRANSACTIONS_BY_CUSTOMER_ID = """
                        SELECT *
                        FROM business_transaction
                        WHERE initiator_customer_id = ?
                        ORDER BY requested_at DESC
                        """;

        /**
         * @brief 按状态列出交易 SQL（List Transactions by Status SQL）；
         *        SQL for listing transactions by status.
         */
        public static final String LIST_TRANSACTIONS_BY_STATUS = """
                        SELECT *
                        FROM business_transaction
                        WHERE transaction_status = ?
                        ORDER BY requested_at DESC
                        """;

        /**
         * @brief 查询全部交易 SQL（Find All Transactions SQL）；
         *        SQL for listing all transactions.
         */
        public static final String FIND_ALL_TRANSACTIONS = "SELECT * FROM business_transaction ORDER BY requested_at DESC";

        /**
         * @brief 按代码查询业务类型 SQL（Find Business Type by Code SQL）；
         *        SQL for finding business type by code.
         */
        public static final String FIND_TYPE_BY_CODE = "SELECT * FROM business_type WHERE business_type_code = ?";

        /**
         * @brief 按 ID 查询业务交易 SQL（Find Business Transaction by ID SQL）；
         *        SQL for finding business transaction by id.
         */
        public static final String FIND_TRANSACTION_BY_ID = "SELECT * FROM business_transaction WHERE transaction_id = ?";

        /**
         * @brief 按参考号查询业务交易 SQL（Find Business Transaction by Reference SQL）；
         *        SQL for finding business transaction by reference number.
         */
        public static final String FIND_TRANSACTION_BY_REFERENCE = "SELECT * FROM business_transaction WHERE reference_no = ?";

        /**
         * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
         *        Utility class should not be instantiated.
         */
        private BusinessSql() {
        }
}
