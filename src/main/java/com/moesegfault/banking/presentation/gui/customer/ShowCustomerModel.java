package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 客户详情页面模型（Show Customer Model），保存 customer_id 输入、查询状态与客户详情；
 *        Show-customer page model storing customer-id input, loading state, and customer details.
 */
public final class ShowCustomerModel extends AbstractGuiModel {

    /**
     * @brief 字段名：客户 ID（Field: Customer ID）；
     *        Field name for customer identifier.
     */
    public static final String FIELD_CUSTOMER_ID = "customer_id";

    /**
     * @brief 当前输入客户 ID（Current Input Customer ID）；
     *        Current customer-id input value.
     */
    private String customerIdInput = "";

    /**
     * @brief 加载中状态（Loading Flag）；
     *        Whether customer query is in progress.
     */
    private boolean loading;

    /**
     * @brief 查询到的客户详情（Resolved Customer）；
     *        Resolved customer detail result.
     */
    private CustomerResult customer;

    /**
     * @brief 未找到客户标记（Customer Not-found Flag）；
     *        Whether last query resulted in customer-not-found.
     */
    private boolean customerNotFound;

    /**
     * @brief 页面错误消息（Page Error Message）；
     *        Page-level error message.
     */
    private String errorMessage;

    /**
     * @brief 设置客户 ID 输入值（Set Customer-id Input）；
     *        Set customer-id input value.
     *
     * @param customerIdInput 客户 ID 输入值（Customer-id input value）。
     */
    public void setCustomerIdInput(final String customerIdInput) {
        this.customerIdInput = Objects.requireNonNullElse(customerIdInput, "");
        fireChanged("customer_id_input");
    }

    /**
     * @brief 返回客户 ID 输入值（Return Customer-id Input）；
     *        Return customer-id input value.
     *
     * @return 客户 ID 输入值（Customer-id input value）。
     */
    public String customerIdInput() {
        return customerIdInput;
    }

    /**
     * @brief 设置加载中状态（Set Loading Flag）；
     *        Set loading flag.
     *
     * @param loading 加载中状态（Loading flag）。
     */
    public void setLoading(final boolean loading) {
        this.loading = loading;
        fireChanged("loading");
    }

    /**
     * @brief 返回加载中状态（Return Loading Flag）；
     *        Return loading flag.
     *
     * @return 加载中状态（Loading flag）。
     */
    public boolean loading() {
        return loading;
    }

    /**
     * @brief 设置查询结果客户（Set Resolved Customer）；
     *        Set resolved customer result.
     *
     * @param customer 查询结果客户（Resolved customer）。
     */
    public void setCustomer(final CustomerResult customer) {
        this.customer = Objects.requireNonNull(customer, "customer must not be null");
        this.customerNotFound = false;
        fireChanged("customer", "customer_not_found");
    }

    /**
     * @brief 清空查询结果客户（Clear Resolved Customer）；
     *        Clear resolved customer result.
     */
    public void clearCustomer() {
        this.customer = null;
        fireChanged("customer");
    }

    /**
     * @brief 返回查询结果客户（Return Resolved Customer）；
     *        Return resolved customer result.
     *
     * @return 客户结果可选值（Optional customer result）。
     */
    public Optional<CustomerResult> customer() {
        return Optional.ofNullable(customer);
    }

    /**
     * @brief 标记客户未找到（Mark Customer Not Found）；
     *        Mark customer-not-found state for last query.
     */
    public void markCustomerNotFound() {
        this.customer = null;
        this.customerNotFound = true;
        fireChanged("customer", "customer_not_found");
    }

    /**
     * @brief 返回客户未找到标记（Return Customer Not-found Flag）；
     *        Return customer-not-found flag.
     *
     * @return 未找到客户标记（Customer not-found flag）。
     */
    public boolean customerNotFound() {
        return customerNotFound;
    }

    /**
     * @brief 设置页面错误消息（Set Page Error Message）；
     *        Set page-level error message.
     *
     * @param errorMessage 错误消息（Error message）。
     */
    public void setErrorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        fireChanged("error_message");
    }

    /**
     * @brief 返回页面错误消息（Return Page Error Message）；
     *        Return page-level error message.
     *
     * @return 错误消息可选值（Optional error message）。
     */
    public Optional<String> errorMessage() {
        return Optional.ofNullable(errorMessage);
    }
}
