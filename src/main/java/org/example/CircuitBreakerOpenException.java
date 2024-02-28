package org.example;

public class CircuitBreakerOpenException extends Exception {
    public CircuitBreakerOpenException(String circuitBreakerIsOpen) {
        super(circuitBreakerIsOpen);
    }
}
