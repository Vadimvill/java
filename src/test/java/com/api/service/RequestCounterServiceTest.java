package com.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class RequestCounterServiceTest {
    @InjectMocks
    RequestCounterService requestCounterService;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void test(){
        requestCounterService.incrementAndGet();
        int i = requestCounterService.get();
        assertEquals(1,i);
    }
}