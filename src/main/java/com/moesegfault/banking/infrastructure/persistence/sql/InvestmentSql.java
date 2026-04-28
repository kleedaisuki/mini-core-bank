package com.moesegfault.banking.infrastructure.persistence.sql;

/**
 * @brief 投资 SQL 常量（Investment SQL Constants）；
 *        Centralized SQL constants for investment persistence.
 */
public final class InvestmentSql {

    /**
     * @brief 投资产品 upsert SQL（Investment Product Upsert SQL）；
     *        SQL for insert-or-update investment product record.
     */
    public static final String UPSERT_PRODUCT = """
            INSERT INTO investment_product (
                product_id,
                product_code,
                product_name,
                product_type,
                currency_code,
                risk_level,
                issuer,
                status
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (product_id) DO UPDATE SET
                product_code = EXCLUDED.product_code,
                product_name = EXCLUDED.product_name,
                product_type = EXCLUDED.product_type,
                currency_code = EXCLUDED.currency_code,
                risk_level = EXCLUDED.risk_level,
                issuer = EXCLUDED.issuer,
                status = EXCLUDED.status
            """;

    /**
     * @brief 投资订单 upsert SQL（Investment Order Upsert SQL）；
     *        SQL for insert-or-update investment order detail record.
     */
    public static final String UPSERT_ORDER = """
            INSERT INTO investment_order_detail (
                transaction_id,
                investment_account_id,
                product_id,
                order_side,
                quantity,
                price,
                gross_amount,
                fee_amount,
                currency_code,
                trade_at,
                settlement_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (transaction_id) DO UPDATE SET
                investment_account_id = EXCLUDED.investment_account_id,
                product_id = EXCLUDED.product_id,
                order_side = EXCLUDED.order_side,
                quantity = EXCLUDED.quantity,
                price = EXCLUDED.price,
                gross_amount = EXCLUDED.gross_amount,
                fee_amount = EXCLUDED.fee_amount,
                currency_code = EXCLUDED.currency_code,
                trade_at = EXCLUDED.trade_at,
                settlement_at = EXCLUDED.settlement_at
            """;

    /**
     * @brief 持仓 upsert SQL（Holding Upsert SQL）；
     *        SQL for insert-or-update holding record.
     */
    public static final String UPSERT_HOLDING = """
            INSERT INTO investment_holding (
                holding_id,
                investment_account_id,
                product_id,
                quantity,
                average_cost,
                cost_currency_code,
                market_value,
                valuation_currency_code,
                unrealized_pnl,
                updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON CONFLICT (holding_id) DO UPDATE SET
                investment_account_id = EXCLUDED.investment_account_id,
                product_id = EXCLUDED.product_id,
                quantity = EXCLUDED.quantity,
                average_cost = EXCLUDED.average_cost,
                cost_currency_code = EXCLUDED.cost_currency_code,
                market_value = EXCLUDED.market_value,
                valuation_currency_code = EXCLUDED.valuation_currency_code,
                unrealized_pnl = EXCLUDED.unrealized_pnl,
                updated_at = EXCLUDED.updated_at
            """;

    /**
     * @brief 产品估值 upsert SQL（Product Valuation Upsert SQL）；
     *        SQL for insert-or-update product valuation record.
     */
    public static final String UPSERT_VALUATION = """
            INSERT INTO product_valuation (
                product_id,
                valuation_date,
                nav,
                currency_code
            ) VALUES (?, ?, ?, ?)
            ON CONFLICT (product_id, valuation_date) DO UPDATE SET
                nav = EXCLUDED.nav,
                currency_code = EXCLUDED.currency_code
            """;

    /**
     * @brief 查询订单详情 SQL（Find Investment Order by ID SQL）；
     *        SQL for loading order detail with business transaction status.
     */
    public static final String FIND_ORDER_BY_ID = """
            SELECT
                iod.*,
                bt.transaction_status AS business_transaction_status
            FROM investment_order_detail iod
            JOIN business_transaction bt ON bt.transaction_id = iod.transaction_id
            WHERE iod.transaction_id = ?
            """;

    /**
     * @brief 查询最新估值 SQL（Find Latest Product Valuation SQL）；
     *        SQL for loading latest valuation by product id.
     */
    public static final String FIND_LATEST_VALUATION = """
            SELECT *
            FROM product_valuation
            WHERE product_id = ?
            ORDER BY valuation_date DESC
            LIMIT 1
            """;

    /**
     * @brief 按 ID 查询投资产品 SQL（Find Product by ID SQL）；
     *        SQL for finding investment product by id.
     */
    public static final String FIND_PRODUCT_BY_ID = "SELECT * FROM investment_product WHERE product_id = ?";

    /**
     * @brief 按代码查询投资产品 SQL（Find Product by Code SQL）；
     *        SQL for finding investment product by code.
     */
    public static final String FIND_PRODUCT_BY_CODE = "SELECT * FROM investment_product WHERE product_code = ?";

    /**
     * @brief 按 ID 查询持仓 SQL（Find Holding by ID SQL）；
     *        SQL for finding holding by id.
     */
    public static final String FIND_HOLDING_BY_ID = "SELECT * FROM investment_holding WHERE holding_id = ?";

    /**
     * @brief 按账户与产品查询持仓 SQL（Find Holding by Account and Product SQL）；
     *        SQL for finding holding by account and product.
     */
    public static final String FIND_HOLDING_BY_ACCOUNT_AND_PRODUCT =
            "SELECT * FROM investment_holding WHERE investment_account_id = ? AND product_id = ?";

    /**
     * @brief 按账户列出持仓 SQL（List Holdings by Account ID SQL）；
     *        SQL for listing holdings by account id.
     */
    public static final String LIST_HOLDINGS_BY_ACCOUNT_ID =
            "SELECT * FROM investment_holding WHERE investment_account_id = ? ORDER BY updated_at DESC";

    /**
     * @brief 按日期查询产品估值 SQL（Find Product Valuation by Date SQL）；
     *        SQL for finding product valuation by product id and date.
     */
    public static final String FIND_VALUATION_BY_DATE =
            "SELECT * FROM product_valuation WHERE product_id = ? AND valuation_date = ?";

    /**
     * @brief 禁止实例化工具类（Non-instantiable Utility Class）；
     *        Utility class should not be instantiated.
     */
    private InvestmentSql() {
    }
}
