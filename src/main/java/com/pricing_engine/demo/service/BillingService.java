package com.pricing_engine.demo.service;

import com.pricing_engine.demo.model.RampSegment;
import com.pricing_engine.demo.repository.RampRepository; // CRITICAL IMPORT
import lombok.RequiredArgsConstructor; // Use this for clean Dependency Injection
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor // Automatically creates the constructor for 'final' fields
public class BillingService {

    private final RampRepository rampRepository;

    public BigDecimal calculateTotalContractValue(List<RampSegment> segments) {
        if (segments == null || segments.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        // Persist segments to H2 database before returning calculation
        rampRepository.saveAll(segments);

        return segments.stream()
                .filter(Objects::nonNull)
                .map(s -> {
                    BigDecimal price = s.getMonthlyPrice() != null ? s.getMonthlyPrice() : BigDecimal.ZERO;
                    BigDecimal duration = s.getDurationMonths() != null ? BigDecimal.valueOf(s.getDurationMonths()) : BigDecimal.ZERO;
                    return price.multiply(duration);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}