package com.pricing_engine.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "ramp_segments")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RampSegment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Let Hibernate handle UUID generation
    @Column(columnDefinition = "UUID", updatable = false, nullable = false)
    private UUID id;

    private BigDecimal monthlyPrice;
    private Integer durationMonths;
}