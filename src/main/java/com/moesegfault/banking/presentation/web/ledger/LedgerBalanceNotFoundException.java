package com.moesegfault.banking.presentation.web.ledger;

/**
 * @brief 账务余额未找到异常（Ledger Balance Not Found Exception），用于触发 Web 404 映射；
 *        Ledger-balance not-found exception used to trigger web 404 mapping.
 */
public final class LedgerBalanceNotFoundException extends RuntimeException {

    /**
     * @brief 构造余额未找到异常（Construct Ledger Balance Not Found Exception）；
     *        Construct ledger-balance not-found exception with message.
     *
     * @param message 异常消息（Exception message）。
     */
    public LedgerBalanceNotFoundException(final String message) {
        super(message);
    }
}
