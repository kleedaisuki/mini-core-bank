package com.moesegfault.banking.infrastructure.gui.swing;

import com.moesegfault.banking.presentation.gui.UiThreadScheduler;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import javax.swing.SwingUtilities;

/**
 * @brief Swing UI 线程调度器（Swing UI Thread Scheduler），基于 EDT（Event Dispatch Thread）执行任务；
 *        Swing implementation of UI thread scheduler backed by EDT.
 */
public final class SwingUiThreadScheduler implements UiThreadScheduler {

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(final Runnable task) {
        SwingUtilities.invokeLater(Objects.requireNonNull(task, "task must not be null"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeAndWait(final Runnable task) {
        final Runnable nonNullTask = Objects.requireNonNull(task, "task must not be null");
        if (isUiThread()) {
            nonNullTask.run();
            return;
        }

        try {
            SwingUtilities.invokeAndWait(nonNullTask);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting Swing EDT task", ex);
        } catch (InvocationTargetException ex) {
            throw new IllegalStateException("Swing EDT task failed", ex.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isUiThread() {
        return SwingUtilities.isEventDispatchThread();
    }
}
