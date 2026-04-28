package com.moesegfault.banking.presentation.gui;

/**
 * @brief UI 线程调度抽象（UI Thread Scheduler Abstraction），负责把任务切换到 UI 主线程；
 *        UI thread scheduler abstraction for dispatching tasks onto toolkit UI thread.
 */
public interface UiThreadScheduler {

    /**
     * @brief 异步执行任务（Execute Asynchronously）；
     *        Schedule one task to run on UI thread asynchronously.
     *
     * @param task 待执行任务（Task to execute）。
     */
    void execute(Runnable task);

    /**
     * @brief 同步执行任务（Execute Synchronously）；
     *        Run one task on UI thread and wait until completion.
     *
     * @param task 待执行任务（Task to execute）。
     */
    void executeAndWait(Runnable task);

    /**
     * @brief 判断当前是否在 UI 线程（Is UI Thread）；
     *        Check whether current thread is toolkit UI thread.
     *
     * @return 在 UI 线程返回 true（True if on UI thread）。
     */
    boolean isUiThread();
}
