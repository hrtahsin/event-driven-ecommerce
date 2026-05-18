package com.eventdriven.platform.order.outbox;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "app.outbox.publisher")
public class OutboxPublisherProperties {

    private boolean enabled = true;
    private int batchSize = 50;
    private long fixedDelay = 5000;
    private Duration sendTimeout = Duration.ofSeconds(5);

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public long getFixedDelay() {
        return fixedDelay;
    }

    public void setFixedDelay(long fixedDelay) {
        this.fixedDelay = fixedDelay;
    }

    public Duration getSendTimeout() {
        return sendTimeout;
    }

    public void setSendTimeout(Duration sendTimeout) {
        this.sendTimeout = sendTimeout;
    }
}
