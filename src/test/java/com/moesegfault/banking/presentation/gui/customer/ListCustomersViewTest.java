package com.moesegfault.banking.presentation.gui.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.moesegfault.banking.application.customer.result.CustomerResult;
import com.moesegfault.banking.infrastructure.gui.swing.view.SwingFormView;
import com.moesegfault.banking.infrastructure.gui.swing.view.SwingTableView;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * @brief 客户列表视图测试（List Customers View Test），验证查询反馈文案；
 *        Tests for customer-list view query feedback messages.
 */
class ListCustomersViewTest {

    /**
     * @brief 验证空结果会展示可感知反馈；
     *        Verify empty query results render visible feedback.
     */
    @Test
    void shouldRenderEmptyResultFeedback() {
        final ListCustomersModel model = new ListCustomersModel();
        final ListCustomersView view = new ListCustomersView(new SwingFormView(), new SwingTableView());

        view.bindModel(model);
        view.mount();
        view.render();

        assertEquals(
                "No customers found. Register a customer first, then refresh this list.",
                view.renderedMessage());

        model.setMobilePhoneFilter("+85255542214");
        model.setCustomers(List.of());

        assertEquals("No customers match mobile phone +85255542214.", view.renderedMessage());
    }

    /**
     * @brief 验证有结果会展示加载数量；
     *        Verify non-empty query results render row-count feedback.
     */
    @Test
    void shouldRenderLoadedResultFeedback() {
        final ListCustomersModel model = new ListCustomersModel();
        final ListCustomersView view = new ListCustomersView(new SwingFormView(), new SwingTableView());

        view.bindModel(model);
        view.mount();
        model.setCustomers(List.of(customerResult("cust-a")));

        assertEquals("Loaded 1 customer.", view.renderedMessage());
    }

    /**
     * @brief 构造客户结果（Build Customer Result）；
     *        Build a customer result for view tests.
     *
     * @param customerId 客户 ID（Customer ID）。
     * @return 客户结果（Customer result）。
     */
    private static CustomerResult customerResult(final String customerId) {
        final Instant now = Instant.parse("2026-05-17T00:00:00Z");
        return new CustomerResult(
                customerId,
                "PASSPORT",
                "E-" + customerId,
                "CN",
                "+8613800000000",
                "Shanghai Pudong",
                "Shanghai Pudong",
                false,
                null,
                "ACTIVE",
                now,
                now);
    }
}
