package com.pricing_engine.demo.service;
 
import com.pricing_engine.demo.model.RampSegment;
import com.pricing_engine.demo.repository.RampRepository; // CRITICAL IMPORT
import lombok.RequiredArgsConstructor; // Use this for clean Dependency Injection
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor // Automatically creates the constructor for 'final' fields
public class BillingService {
 
    private final RampRepository rampRepository;
 
    public BigDecimal calculateTotalContractValue(List<RampSegment> segments) {
        if (segments == null || segments.isEmpty()) {
            return BigDecimal.ZERO;
        }
 
        // Filter null elements FIRST before any database or calculation work
        List<RampSegment> validSegments = segments.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
 
        // Persist only the clean, validated list to H2 database
        rampRepository.saveAll(validSegments);
 
        return validSegments.stream()
                .map(s -> {
                    BigDecimal price = s.getMonthlyPrice() != null ? s.getMonthlyPrice() : BigDecimal.ZERO;
                    BigDecimal duration = s.getDurationMonths() != null ? BigDecimal.valueOf(s.getDurationMonths()) : BigDecimal.ZERO;
                    return price.multiply(duration);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}