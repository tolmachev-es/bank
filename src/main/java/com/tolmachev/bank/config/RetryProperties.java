package com.tolmachev.bank.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Data
@RefreshScope
@Component
public class RetryProperties {
    @Value("${wallet.retry.max-attempts}")
    private int maxAttempts;
    @Value("${wallet.retry.backoff-period}")
    private int backoffPeriod;
}
