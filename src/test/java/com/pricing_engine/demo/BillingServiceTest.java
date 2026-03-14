package com.pricing_engine.demo;

import com.pricing_engine.demo.model.RampSegment;
import com.pricing_engine.demo.repository.RampRepository;
import com.pricing_engine.demo.service.BillingService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class BillingServiceTest {

    private BillingService billingService;
    private RampRepository rampRepository;

    @BeforeEach
    void setUp() {
        // 1. Create a mock of the repository
        rampRepository = mock(RampRepository.class);
        
        // 2. Inject the mock into the service
        billingService = new BillingService(rampRepository);
    }

    @Test
    void testCalculateTCV() {
        RampSegment s1 = new RampSegment();
        s1.setMonthlyPrice(new BigDecimal("100"));
        s1.setDurationMonths(12);

        BigDecimal result = billingService.calculateTotalContractValue(List.of(s1));
        
        // Assert that 100 * 12 = 1200
        assertEquals(0, new BigDecimal("1200").compareTo(result));
    }
}