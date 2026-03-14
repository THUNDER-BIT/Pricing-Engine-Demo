package com.pricing_engine.demo.repository;

import com.pricing_engine.demo.model.RampSegment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

/**
 * Repository for persisting RampSegment entities as used by BillingService.
 */
@Repository
public interface RampRepository extends JpaRepository<RampSegment, UUID> {
}