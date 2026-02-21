# Axon Framework 5 DCB Demo

Comparison of traditional Axon aggregates vs Dynamic Consistency Boundary (DCB) pattern.

## Scenario: Financial Account Management

### Phase 1: Simple Bank Account (Traditional Aggregate)
- Single `BankAccount` aggregate
- Deposits and withdrawals
- Works great initially

### Phase 2: Business Evolution - Multiple Account Types
- Business adds: Savings, Investment, Loan accounts
- Problem: `BankAccount` name doesn't fit anymore
- Traditional solution: Complex event migration or duplicate aggregates

### Phase 3: Cross-Account Transfers
- Transfer money between accounts
- Problem: Consistency boundary spans TWO accounts
- Traditional solution: Heavyweight saga orchestration
- DCB solution: Operation-specific consistency boundary

## Project Structure

```
src/
├── main/
│   └── java/
│       └── nl/maxzilla/axon/
│           ├── traditional/          # Axon 4-style (rigid aggregates)
│           │   ├── BankAccount.java
│           │   └── TransferSaga.java
│           ├── dcb/                  # Axon 5 DCB (flexible boundaries)
│           │   ├── Account.java      # Plain entity
│           │   └── TransferDCB.java  # Dynamic boundary
│           ├── commands/
│           ├── events/
│           └── Application.java
└── test/
    └── java/
        └── nl/maxzilla/axon/
            ├── TraditionalAggregateTest.java
            └── DCBTest.java
```

## Running the Demo

### Prerequisites
- Java 21+
- Maven 3.6+
- Docker (for Axon Server)

### Start Axon Server
```bash
docker run -d --name axonserver \
  -p 8024:8024 -p 8124:8124 \
  axoniq/axonserver:2025.1.0
```

### Build and Run
```bash
mvn clean install
mvn spring-boot:run
```

### Run Tests
```bash
mvn test
```

## Key Differences

### Traditional Aggregate (Axon 4 style)

**Pros:**
- Simple for single-entity operations
- Well-understood pattern
- Strong consistency guarantees

**Cons:**
- Aggregate definition is permanent
- Cross-entity operations require sagas
- Business evolution forces architectural changes

### Dynamic Consistency Boundary (Axon 5)

**Pros:**
- Operations can span multiple entities
- Aggregates can evolve without event migration
- Simpler than sagas for cross-entity coordination

**Cons:**
- More complex initial setup
- Requires Axon Framework 5.0.0-M3+
- Still experimental (milestone release)

## Blog Post

This code supports the blog post:
[Axon Framework 5's Dynamic Consistency Boundary Fixes Event Sourcing's Biggest Flaw](https://www.maxzilla.nl/blog/axon-5-dynamic-consistency-boundary)

## License

MIT
