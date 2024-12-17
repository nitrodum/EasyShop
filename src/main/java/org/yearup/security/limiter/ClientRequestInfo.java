package org.yearup.security.limiter;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientRequestInfo {
    private long startTime;
    private final AtomicInteger requestCount;

    public ClientRequestInfo() {
        this.startTime = Instant.now().toEpochMilli();
        this.requestCount = new AtomicInteger(0);
    }

    public long getStartTime() {
        return startTime;
    }

    public int getRequestCount() {
        return requestCount.get();
    }

    public void incrementRequestCount() {
        requestCount.incrementAndGet();
    }

    public void reset(long newStartTime) {
        this.startTime = newStartTime;
        this.requestCount.set(0);
    }
}
