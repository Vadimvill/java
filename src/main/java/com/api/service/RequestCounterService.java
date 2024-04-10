package com.api.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
public class RequestCounterService {
    private AtomicInteger count;

    public RequestCounterService() {
        count = new AtomicInteger(0);
    }

    public int incrementAndGet() {
       return count.incrementAndGet();
    }

    public int get() {
        return count.get();
    }
}
