package com.tolmachev.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DeadlockLoserDataAccessException;
import org.springframework.stereotype.Component;


@Component
public class RetryExecutor {
    private final RetryProperties retryProperties;

    @Autowired
    public RetryExecutor(RetryProperties retryProperties) {
        this.retryProperties = retryProperties;
    }

    public void execute(Runnable action) {
        int maxAttempts = retryProperties.getMaxAttempts();
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                action.run();
                return;
            } catch (Exception ex) {
                if (!isRetryable(ex)) {
                    throw ex;
                }
                if (attempt == maxAttempts) {
                    throw ex;
                }
                sleep(retryProperties.getBackoffPeriod());
            }
        }
    }

    private boolean isRetryable(Throwable ex) {
        Throwable root = unwrap(ex);
        return root instanceof CannotAcquireLockException
                || root instanceof DeadlockLoserDataAccessException;
    }

    private Throwable unwrap(Throwable ex) {
        while (ex.getCause() != null && ex != ex.getCause()) {
            ex = ex.getCause();
        }
        return ex;
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}