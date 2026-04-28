package com.moesegfault.banking.domain.investment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @brief 投资领域仓储接口（Investment Repository Interface），定义产品/订单/持仓/估值持久化契约；
 *        Investment repository interface defining persistence contracts for products, orders, holdings and valuations.
 */
public interface InvestmentRepository {

    /**
     * @brief 保存投资产品（Save Investment Product）；
     *        Save investment product entity.
     *
     * @param investmentProduct 投资产品实体（Investment-product entity）。
     */
    void saveInvestmentProduct(InvestmentProduct investmentProduct);

    /**
     * @brief 保存投资订单（Save Investment Order）；
     *        Save investment order entity.
     *
     * @param investmentOrder 投资订单实体（Investment-order entity）。
     */
    void saveInvestmentOrder(InvestmentOrder investmentOrder);

    /**
     * @brief 保存持仓（Save Holding）；
     *        Save holding entity.
     *
     * @param holding 持仓实体（Holding entity）。
     */
    void saveHolding(Holding holding);

    /**
     * @brief 保存产品估值（Save Product Valuation）；
     *        Save product valuation entity.
     *
     * @param productValuation 产品估值实体（Product-valuation entity）。
     */
    void saveProductValuation(ProductValuation productValuation);

    /**
     * @brief 按产品 ID 查询产品（Find Product by ID）；
     *        Find product by product ID.
     *
     * @param productId 产品 ID（Product ID）。
     * @return 产品可选值（Optional product）。
     */
    Optional<InvestmentProduct> findProductById(ProductId productId);

    /**
     * @brief 按产品代码查询产品（Find Product by Code）；
     *        Find product by product code.
     *
     * @param productCode 产品代码（Product code）。
     * @return 产品可选值（Optional product）。
     */
    Optional<InvestmentProduct> findProductByCode(ProductCode productCode);

    /**
     * @brief 按订单 ID 查询订单（Find Order by ID）；
     *        Find order by order ID.
     *
     * @param investmentOrderId 订单 ID（Order ID）。
     * @return 订单可选值（Optional order）。
     */
    Optional<InvestmentOrder> findOrderById(InvestmentOrderId investmentOrderId);

    /**
     * @brief 按持仓 ID 查询持仓（Find Holding by ID）；
     *        Find holding by holding ID.
     *
     * @param holdingId 持仓 ID（Holding ID）。
     * @return 持仓可选值（Optional holding）。
     */
    Optional<Holding> findHoldingById(HoldingId holdingId);

    /**
     * @brief 按账户与产品查询持仓（Find Holding by Account and Product）；
     *        Find holding by investment account and product.
     *
     * @param investmentAccountId 投资账户 ID（Investment account ID）。
     * @param productId           产品 ID（Product ID）。
     * @return 持仓可选值（Optional holding）。
     */
    Optional<Holding> findHoldingByAccountAndProduct(
            InvestmentAccountId investmentAccountId,
            ProductId productId);

    /**
     * @brief 查询账户下全部持仓（List Holdings by Account）；
     *        List holdings by investment account.
     *
     * @param investmentAccountId 投资账户 ID（Investment account ID）。
     * @return 持仓列表（Holding list）。
     */
    List<Holding> listHoldingsByAccountId(InvestmentAccountId investmentAccountId);

    /**
     * @brief 查询某日产品估值（Find Product Valuation by Date）；
     *        Find product valuation by product and date.
     *
     * @param productId      产品 ID（Product ID）。
     * @param valuationDate 估值日期（Valuation date）。
     * @return 估值可选值（Optional valuation）。
     */
    Optional<ProductValuation> findProductValuationByDate(
            ProductId productId,
            LocalDate valuationDate);

    /**
     * @brief 查询产品最新估值（Find Latest Product Valuation）；
     *        Find latest valuation of a product.
     *
     * @param productId 产品 ID（Product ID）。
     * @return 最新估值可选值（Optional latest valuation）。
     */
    Optional<ProductValuation> findLatestProductValuation(ProductId productId);
}
