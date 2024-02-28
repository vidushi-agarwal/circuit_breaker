package org.example;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CircuitBreakerTest {

    @Test
    public void testHappyPath()throws  Exception {
        CircuitBreaker circuitBreaker = new CircuitBreaker(5, 2000);
        String a = circuitBreaker.execute(new Supplier<String>() {
            @Override
            public String get() {
                return "test string";
            }
        });

        assertEquals(a,"test string");
    }

    @Test
    public void testCircuitBreakerException() {
        CircuitBreaker circuitBreaker = new CircuitBreaker(5, 2000);
        try {
            circuitBreaker.execute(new Supplier<Object>() {
                @Override
                public String get() {
                    throw new RuntimeException("supplier failed");
                }
            });
        } catch (Exception e) {
            assertEquals(e.getMessage(),"supplier failed");
        }
    }

    @Test
    public void testRunTimeException() {
        CircuitBreaker circuitBreaker = new CircuitBreaker(5, 2000);
        assertThrows( RuntimeException.class, ()-> {
            circuitBreaker.execute(new Supplier<Object>() {
                @Override
                public String get() {
                    throw new RuntimeException("supplier failed");
                }
            });
        });
        assertEquals(circuitBreaker.getTotalCount().get(),1);

    }
    @Test
    public void testCircuitBreakerException1() throws Exception {
        CircuitBreaker circuitBreaker = new CircuitBreaker(5, 2000);
        for( int i=0;i<5;i++) {
            assertThrows( RuntimeException.class, ()-> {
                   circuitBreaker.execute(new Supplier<Object>() {
                       @Override
                       public String get() {
                           throw new RuntimeException("supplier failed");
                       }
                   });

            });
        }
        assertThrows( CircuitBreakerOpenException.class, ()-> {
            circuitBreaker.execute(new Supplier<Object>() {
                @Override
                public String get() {
                    throw new RuntimeException("supplier failed");
                }
            });

        });
        Long lastOpenedTime = circuitBreaker.getLastTimeWhenOpen();
        Long currentTime = circuitBreaker.getCurrentTime();
        System.out.println(lastOpenedTime+" "+currentTime);

        Thread.sleep(2000);
        assertThrows( RuntimeException.class, ()-> {
                    circuitBreaker.execute(new Supplier<Object>() {
                        @Override
                        public String get() {
                            throw new RuntimeException("supplier failed");
                        }
                    });
                }
        );

        Long currentTime2 = circuitBreaker.getCurrentTime();
        System.out.println(currentTime2);
        assertEquals(circuitBreaker.getCircuit(),0);

    }

}
