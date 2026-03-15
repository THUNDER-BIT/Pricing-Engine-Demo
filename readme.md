# Revenue Pricing Engine

A precision-focused billing tool that calculates **Total Contract Value (TCV)** for multi-year ramp deals - the kind commonly used in enterprise software sales.

Built to demonstrate production-grade Spring Boot development: 3-tier architecture, financial precision using BigDecimal, full test coverage, and CI/CD via GitHub Actions.


## Quick Start

git clone https://github.com/THUNDER-BIT/Revenue-Pricing-Engine.git
cd Revenue-Pricing-Engine
./mvnw spring-boot:run

Open (http://localhost:8080) in your browser.

---

## Example

| Segment | Monthly Price | Duration | Value |
|---------|--------------|----------|-------|
| Year 1  | $1,000 | 12 months | $12,000 |
| Year 2  | $1,500 | 12 months | $18,000 |
| Year 3  | $2,000 | 12 months | $24,000 |
| **TCV** | | | **$54,000** |

---

## API
```bash
POST /api/v1/billing/calculate
Content-Type: application/json

[
  { "monthlyPrice": 1000.00, "durationMonths": 12 },
  { "monthlyPrice": 1500.00, "durationMonths": 12 },
  { "monthlyPrice": 2000.00, "durationMonths": 12 }
]

→ 54000.00
```

---

## Stack

Java 17 · Spring Boot 3.5 · Thymeleaf · Bootstrap 5 · Spring Data JPA · H2 · Maven · GitHub Actions

---

## How It's Built

3-tier architecture — UI talks to a REST controller, controller delegates to the service, service handles math and persistence.

Two decisions worth calling out:

**BigDecimal for all math** — standard floating-point arithmetic gives `0.1 + 0.2 = 0.30000000000000004`. That's fine for most things, not for billing. BigDecimal is exact.

**Null filtering before database writes** — caught during testing. The original code called `saveAll()` before filtering null elements, which would throw an exception before any calculation happened. Fixed by filtering first, then persisting.
