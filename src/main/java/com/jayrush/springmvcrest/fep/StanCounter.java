package com.jayrush.springmvcrest.fep;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ... on 24/11/2018.
 */
@Component
public class StanCounter {

    private AtomicLong counter = new AtomicLong();

    public synchronized String getStan() {
        if (counter.intValue() > 999999L) {
            counter.set(0);
        }
        return String.format("%06d", counter.incrementAndGet());
    }

    public synchronized static String getId() {
        AtomicLong counter = new AtomicLong();
        if (counter.intValue() > 999999L) {
            counter.set(0);
        }
        return String.format("%06d", counter.incrementAndGet());
    }
}
