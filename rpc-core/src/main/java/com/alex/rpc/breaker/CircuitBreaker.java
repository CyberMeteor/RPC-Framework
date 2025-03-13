package com.alex.rpc.breaker;

import java.util.concurrent.atomic.AtomicInteger;

public class CircuitBreaker {
    private State state = State.CLOSED;
    private final AtomicInteger failCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger total = new AtomicInteger(0);
    private final int failThreshold;
    private final double successRateInHalfOpen;
    // Circuit break time window
    private final long windowTime;
    private long lastFailTime = 0;

    public CircuitBreaker(int failThreshold, double successRateInHalfOpen, long windowTime) {
        this.failThreshold = failThreshold;
        this.successRateInHalfOpen = successRateInHalfOpen;
        this.windowTime = windowTime;
    }

    public synchronized boolean canReq() {
        switch (state) {
            case CLOSED:
                return true;
            case OPEN:
                if (System.currentTimeMillis() - lastFailTime <= windowTime) {
                    return false;
                }

                state = State.HALF_OPEN;
                resetCount();
                return true;
            case HALF_OPEN:
                total.incrementAndGet();
                return true;
            default:
                throw new IllegalStateException("Circuit breaker error");
        }
    }

    public synchronized void success() {
        if (state != State.HALF_OPEN){
            resetCount();
            return;
        }
        successCount.incrementAndGet();
        if (successCount.get() >= successRateInHalfOpen * total.get()) {
            state = State.CLOSED;
            resetCount();
        }
    }

    public synchronized void fail() {
        failCount.incrementAndGet();
        lastFailTime = System.currentTimeMillis();

        if (state == State.HALF_OPEN) {
            state = State.OPEN;
            return;
        }

        if (failCount.get() >= failThreshold) {
            state = State.OPEN;
        }
    }


    private void resetCount() {
        failCount.set(0);
        successCount.set(0);
        total.set(0);
    }

    enum State {
        OPEN,
        HALF_OPEN,
        CLOSED
    }
}
