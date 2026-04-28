package com.moesegfault.banking.presentation.gui;

/**
 * @brief UI 线程调度接口（UI Thread Scheduler Interface），定义主线程任务切换能力；
 *        UI thread scheduler contract defining main-thread task dispatch capability.
 */
public interface UiThreadScheduler {

    /**
     * @brief 异步在 UI 线程执行任务（Execute Task Asynchronously On UI Thread）；
     *        Execute one task asynchronously on UI main thread.
     *
     * @param task 待执行任务（Task to execute）。
     */
    void execute(Runnable task);

    /**
     * @brief 同步在 UI 线程执行任务（Execute Task Synchronously On UI Thread）；
     *        Execute one task synchronously on UI main thread.
     *
     * @param task 待执行任务（Task to execute）。
     */
    void executeAndWait(Runnable task);

    /**
     * @brief 判断当前线程是否 UI 线程（Check UI Thread）；
     *        Check whether current thread is UI thread.
     *
     * @return 是否 UI 线程（Whether current thread is UI thread）。
     */
    boolean isUiThread();

    /**
     * @brief 在 UI 线程执行任务别名（Alias Of Run On UI Thread）；
     *        Alias method for running task on UI thread.
     *
     * @param task 待执行任务（Task to execute）。
     */
    default void runOnUiThread(final Runnable task) {
        execute(task);
    }
}
