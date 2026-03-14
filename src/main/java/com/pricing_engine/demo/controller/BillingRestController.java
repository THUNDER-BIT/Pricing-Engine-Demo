package com.pricing_engine.demo.controller;

import com.pricing_engine.demo.model.RampSegment;
import com.pricing_engine.demo.service.BillingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/billing")
public class BillingRestController {

    private final BillingService billingService;

    // Spring automatically injects the service here
    public BillingRestController(BillingService billingService) {
        this.billingService = billingService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<BigDecimal> calculate(@RequestBody List<RampSegment> segments) {
        if (segments == null) {
            return ResponseEntity.badRequest().build();
        }
        BigDecimal total = billingService.calculateTotalContractValue(segments);
        return ResponseEntity.ok(total);
    }
}