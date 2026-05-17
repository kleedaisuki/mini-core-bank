package com.moesegfault.banking.presentation.cli.builtin;

/**
 * @brief 内建退出请求（Built-in Exit Request），由 `:exit` 与 `:quit` 通知 Shell 正常退出；
 *        Built-in exit request used by `:exit` and `:quit` to signal normal shell termination.
 */
public final class BuiltinExitRequested extends RuntimeException {

    /**
     * @brief 构造退出请求（Construct Exit Request）；
     *        Construct exit request without stack trace overhead.
     */
    public BuiltinExitRequested() {
        super("Builtin exit requested", null, false, false);
    }
}
