package com.tolmachev.bank.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@Data
@RefreshScope
@ConfigurationProperties(prefix = "wallet.retry")
public class RetryProperties {
    private int maxAttempts;
    private int backoffPeriod;
}
