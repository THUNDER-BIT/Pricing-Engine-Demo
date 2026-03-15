package com.pricing_engine.demo.service;

import com.pricing_engine.demo.model.RampSegment;
import com.pricing_engine.demo.repository.RampRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BillingService Unit Tests")
class BillingServiceTest {

    @Mock
    private RampRepository rampRepository;

    @InjectMocks
    private BillingService billingService;

    @Test
    @DisplayName("Single segment: price x duration = correct TCV")
    void calculate_singleSegment_returnsCorrectValue() {
        RampSegment segment = RampSegment.builder()
                .monthlyPrice(new BigDecimal("1000.00"))
                .durationMonths(12)
                .build();

        BigDecimal result = billingService.calculateTotalContractValue(List.of(segment));

        assertThat(result).isEqualByComparingTo(new BigDecimal("12000.00"));
    }

    @Test
    @DisplayName("Multi-segment ramp deal: sums all segment values correctly")
    void calculate_multipleSegments_returnsCorrectTCV() {
        List<RampSegment> segments = List.of(
                RampSegment.builder().monthlyPrice(new BigDecimal("500.00")).durationMonths(12).build(),
                RampSegment.builder().monthlyPrice(new BigDecimal("800.00")).durationMonths(12).build()
        );

        BigDecimal result = billingService.calculateTotalContractValue(segments);

        assertThat(result).isEqualByComparingTo(new BigDecimal("15600.00"));
    }

    @Test
    @DisplayName("Three-year ramp deal: all three segments summed correctly")
    void calculate_threeYearRampDeal_returnsTotalTCV() {
        List<RampSegment> segments = List.of(
                RampSegment.builder().monthlyPrice(new BigDecimal("1000.00")).durationMonths(12).build(),
                RampSegment.builder().monthlyPrice(new BigDecimal("1500.00")).durationMonths(12).build(),
                RampSegment.builder().monthlyPrice(new BigDecimal("2000.00")).durationMonths(12).build()
        );

        BigDecimal result = billingService.calculateTotalContractValue(segments);

        assertThat(result).isEqualByComparingTo(new BigDecimal("54000.00"));
    }

    @Test
    @DisplayName("BigDecimal precision: avoids floating-point arithmetic errors")
    void calculate_highPrecisionValues_maintainsExactResult() {
        RampSegment segment = RampSegment.builder()
                .monthlyPrice(new BigDecimal("99.99"))
                .durationMonths(3)
                .build();

        BigDecimal result = billingService.calculateTotalContractValue(List.of(segment));

        assertThat(result).isEqualByComparingTo(new BigDecimal("299.97"));
    }

    @Test
    @DisplayName("BigDecimal precision: large enterprise deal values stay exact")
    void calculate_largeValues_staysExact() {
        RampSegment segment = RampSegment.builder()
                .monthlyPrice(new BigDecimal("49999.99"))
                .durationMonths(36)
                .build();

        BigDecimal result = billingService.calculateTotalContractValue(List.of(segment));

        assertThat(result).isEqualByComparingTo(new BigDecimal("1799999.64"));
    }

    @Test
    @DisplayName("Null list returns ZERO with no database call")
    void calculate_nullList_returnsZeroWithNoDatabaseInteraction() {
        BigDecimal result = billingService.calculateTotalContractValue(null);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        verify(rampRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Empty list returns ZERO with no database call")
    void calculate_emptyList_returnsZeroWithNoDatabaseInteraction() {
        BigDecimal result = billingService.calculateTotalContractValue(Collections.emptyList());

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        verify(rampRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Segment with null monthlyPrice is treated as ZERO")
    void calculate_nullMonthlyPrice_treatsAsZeroContribution() {
        RampSegment segment = RampSegment.builder()
                .monthlyPrice(null)
                .durationMonths(12)
                .build();

        BigDecimal result = billingService.calculateTotalContractValue(List.of(segment));

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Segment with null durationMonths is treated as ZERO")
    void calculate_nullDurationMonths_treatsAsZeroContribution() {
        RampSegment segment = RampSegment.builder()
                .monthlyPrice(new BigDecimal("500.00"))
                .durationMonths(null)
                .build();

        BigDecimal result = billingService.calculateTotalContractValue(List.of(segment));

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Segment with zero price contributes nothing to TCV")
    void calculate_zeroMonthlyPrice_contributesZeroToTotal() {
        RampSegment segment = RampSegment.builder()
                .monthlyPrice(BigDecimal.ZERO)
                .durationMonths(12)
                .build();

        BigDecimal result = billingService.calculateTotalContractValue(List.of(segment));

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Mixed valid and null-field segments: only valid ones contribute")
    void calculate_mixedValidAndNullFieldSegments_onlyValidContribute() {
        List<RampSegment> segments = List.of(
                RampSegment.builder().monthlyPrice(new BigDecimal("500.00")).durationMonths(12).build(),
                RampSegment.builder().monthlyPrice(null).durationMonths(6).build()
        );

        BigDecimal result = billingService.calculateTotalContractValue(segments);

        assertThat(result).isEqualByComparingTo(new BigDecimal("6000.00"));
    }

    @Test
    @DisplayName("Valid segments: saveAll is called exactly once")
    void calculate_validSegments_persistsToDatabase() {
        List<RampSegment> segments = List.of(
                RampSegment.builder().monthlyPrice(new BigDecimal("100.00")).durationMonths(6).build()
        );

        billingService.calculateTotalContractValue(segments);

        verify(rampRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Null element in list is filtered out, calculation still succeeds")
    void calculate_nullElementInList_isFilteredCorrectly() {
        List<RampSegment> segmentsWithNull = Arrays.asList(
                RampSegment.builder().monthlyPrice(new BigDecimal("500.00")).durationMonths(12).build(),
                null
        );

        BigDecimal result = billingService.calculateTotalContractValue(segmentsWithNull);

        assertThat(result).isEqualByComparingTo(new BigDecimal("6000.00"));
    }
}