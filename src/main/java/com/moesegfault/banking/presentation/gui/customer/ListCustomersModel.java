package com.moesegfault.banking.presentation.gui.customer;

import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.presentation.gui.mvc.AbstractGuiModel;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @brief 客户列表页面模型（List Customers Model），保存筛选条件、列表结果、选中行和页面状态；
 *        Customer-list page model storing filter, list result, selected row, and page status.
 */
public final class ListCustomersModel extends AbstractGuiModel {

    /**
     * @brief 字段名：手机号筛选（Field: Mobile Phone Filter）；
     *        Field name for mobile-phone filter.
     */
    public static final String FIELD_MOBILE_PHONE = "mobile_phone";

    /**
     * @brief 当前手机号筛选值（Current Mobile-phone Filter）；
     *        Current mobile-phone filter value.
     */
    private String mobilePhoneFilter = "";

    /**
     * @brief 客户列表（Customer List）；
     *        Current customer list.
     */
    private List<CustomerResult> customers = List.of();

    /**
     * @brief 选中行下标（Selected Row Index）；
     *        Selected row index in customer list.
     */
    private Integer selectedRowIndex;

    /**
     * @brief 加载中状态（Loading Flag）；
     *        Whether list query is loading.
     */
    private boolean loading;

    /**
     * @brief 页面错误消息（Page Error Message）；
     *        Page-level error message.
     */
    private String errorMessage;

    /**
     * @brief 设置手机号筛选值（Set Mobile-phone Filter）；
     *        Set mobile-phone filter value.
     *
     * @param mobilePhoneFilter 手机号筛选值（Mobile-phone filter value）。
     */
    public void setMobilePhoneFilter(final String mobilePhoneFilter) {
        this.mobilePhoneFilter = Objects.requireNonNullElse(mobilePhoneFilter, "");
        fireChanged("mobile_phone_filter");
    }

    /**
     * @brief 返回手机号筛选值（Return Mobile-phone Filter）；
     *        Return mobile-phone filter value.
     *
     * @return 手机号筛选值（Mobile-phone filter value）。
     */
    public String mobilePhoneFilter() {
        return mobilePhoneFilter;
    }

    /**
     * @brief 设置客户列表（Set Customer List）；
     *        Set customer list result.
     *
     * @param customers 客户列表（Customer list）。
     */
    public void setCustomers(final List<CustomerResult> customers) {
        this.customers = List.copyOf(Objects.requireNonNull(customers, "customers must not be null"));
        if (selectedRowIndex != null && selectedRowIndex >= this.customers.size()) {
            selectedRowIndex = null;
        }
        fireChanged("customers", "selected_row_index");
    }

    /**
     * @brief 返回客户列表（Return Customer List）；
     *        Return customer list.
     *
     * @return 客户列表（Customer list）。
     */
    public List<CustomerResult> customers() {
        return List.copyOf(customers);
    }

    /**
     * @brief 设置选中行下标（Set Selected Row Index）；
     *        Set selected row index.
     *
     * @param selectedRowIndex 选中行下标（Selected row index）。
     */
    public void setSelectedRowIndex(final Integer selectedRowIndex) {
        if (selectedRowIndex == null) {
            this.selectedRowIndex = null;
            fireChanged("selected_row_index");
            return;
        }

        if (selectedRowIndex < 0 || selectedRowIndex >= customers.size()) {
            throw new IllegalArgumentException("selectedRowIndex out of range: " + selectedRowIndex);
        }

        this.selectedRowIndex = selectedRowIndex;
        fireChanged("selected_row_index");
    }

    /**
     * @brief 返回选中行下标（Return Selected Row Index）；
     *        Return selected row index.
     *
     * @return 选中行下标可选值（Optional selected row index）。
     */
    public Optional<Integer> selectedRowIndex() {
        return Optional.ofNullable(selectedRowIndex);
    }

    /**
     * @brief 返回选中客户 ID（Return Selected Customer ID）；
     *        Return selected customer ID if one row is selected.
     *
     * @return 客户 ID 可选值（Optional customer ID）。
     */
    public Optional<String> selectedCustomerId() {
        if (selectedRowIndex == null || selectedRowIndex < 0 || selectedRowIndex >= customers.size()) {
            return Optional.empty();
        }
        return Optional.of(customers.get(selectedRowIndex).customerId());
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
