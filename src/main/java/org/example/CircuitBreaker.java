package org.example;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;


@Data
public class CircuitBreaker {
    private  final int MAX_NUM_ERRORS;
    private final AtomicInteger totalCount;
    private long lastTimeWhenOpen;
    private long currentTime;

    private final int timeCloseWindow;

    private int circuit;

    public CircuitBreaker(int MAX_NUM_ERRORS, int timeCloseWindow) {
        this.MAX_NUM_ERRORS = MAX_NUM_ERRORS;
        this.totalCount = new AtomicInteger(0);
        this.timeCloseWindow = timeCloseWindow;
        this.circuit = 0; //closed
        this.lastTimeWhenOpen = System.currentTimeMillis();
        this.currentTime = System.currentTimeMillis();
    }
    public <T> T execute(Supplier<T> supplier) throws Exception {
        try {
            if(circuit == 1) {
                if(!resetIfPossible(totalCount, timeCloseWindow))
                throw new CircuitBreakerOpenException("Circuit Breaker is open");
            }
            return supplier.get();
        }
        catch (Exception e) {
            totalCount.incrementAndGet();
            if (totalCount.get() == MAX_NUM_ERRORS) {
                circuit = 1; //open
                lastTimeWhenOpen = System.currentTimeMillis();
            }
            throw e;
        }
    }
    private boolean resetIfPossible(AtomicInteger totalCount, int timeCloseWindow){
        totalCount.incrementAndGet();
        currentTime = System.currentTimeMillis();
        if(currentTime-lastTimeWhenOpen>=timeCloseWindow) {
            circuit = 0;
            totalCount.set(0);
            return true;
        }
        return false;
    }
}
