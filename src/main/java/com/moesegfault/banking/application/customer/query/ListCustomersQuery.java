package com.moesegfault.banking.application.customer.query;

/**
 * @brief 客户列表查询请求（List Customers Query），支持全量或按手机号过滤；
 *        Customer-list query request supporting all-customers or mobile-phone filtering.
 */
public final class ListCustomersQuery {

    /**
     * @brief 手机号过滤条件（Mobile Phone Filter）；
     *        Mobile-phone filter, nullable.
     */
    private final String mobilePhone;

    /**
     * @brief 使用手机号过滤构造查询（Construct with Mobile-Phone Filter）；
     *        Construct query with optional mobile-phone filter.
     *
     * @param mobilePhone 手机号过滤条件（Mobile-phone filter, nullable）。
     */
    private ListCustomersQuery(final String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    /**
     * @brief 创建全量查询（Create Query for All Customers）；
     *        Create query for fetching all customers.
     *
     * @return 全量查询对象（All-customers query object）。
     */
    public static ListCustomersQuery all() {
        return new ListCustomersQuery(null);
    }

    /**
     * @brief 创建手机号过滤查询（Create Query by Mobile Phone）；
     *        Create query filtering by mobile phone.
     *
     * @param mobilePhone 手机号过滤条件（Mobile-phone filter）。
     * @return 过滤查询对象（Filtered query object）。
     */
    public static ListCustomersQuery byMobilePhone(final String mobilePhone) {
        return new ListCustomersQuery(mobilePhone);
    }

    /**
     * @brief 返回手机号过滤条件（Return Mobile-Phone Filter）；
     *        Return mobile-phone filter.
     *
     * @return 手机号过滤条件（Mobile-phone filter, nullable）。
     */
    public String mobilePhone() {
        return mobilePhone;
    }

    /**
     * @brief 判断是否使用手机号过滤（Check Mobile-Phone Filter Existence）；
     *        Check whether a mobile-phone filter exists.
     *
     * @return 存在过滤条件返回 true（true when filter exists）。
     */
    public boolean hasMobilePhoneFilter() {
        return mobilePhone != null && !mobilePhone.isBlank();
    }
}
