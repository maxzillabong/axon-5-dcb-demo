# Axon Framework 5 Dynamic Consistency Boundary Demo

Demonstration of how Dynamic Consistency Boundary (DCB) in Axon Framework 5 enables cross-entity operations without sagas.

## What This Demonstrates

**Traditional Axon (4.x):**
- Aggregates = fixed consistency boundaries
- Cross-entity operations require Sagas
- ~120 LOC for a simple transfer (with error handling)

**Axon 5 with DCB:**
- Dynamic consistency boundaries per operation
- Multiple entities in one transaction
- ~30 LOC for the same transfer

## Real DCB API

The code in this repository uses the **actual Axon Framework 5 API**:

```java
// 1. Events tagged with multiple entity IDs
public record MoneyTransferredEvent(
    @EventTag(key = "fromAccountId") String fromAccountId,
    @EventTag(key = "toAccountId") String toAccountId,
    String transferId,
    BigDecimal amount
) {}

// 2. Entities that respond to tagged events
@EventSourcedEntity(tagKey = "accountId")
public class Account {
    @EventSourcingHandler
    void evolve(MoneyTransferredEvent event) {
        // Updates when event matches this account's tag
    }
}

// 3. Command handler with multiple injected entities
@CommandHandler
void handle(
    TransferMoneyCommand command,
    @InjectEntity(idProperty = "fromAccountId") Account fromAccount,
    @InjectEntity(idProperty = "toAccountId") Account toAccount,
    EventAppender eventAppender
) {
    // Both accounts in consistency boundary
    fromAccount.withdraw(command.amount());
    toAccount.deposit(command.amount());
    
    eventAppender.append(new MoneyTransferredEvent(...));
}
```

## Key Annotations

| Annotation | Purpose |
|------------|---------|
| `@EventTag(key = "...")` | Tags event with entity ID for stream filtering |
| `@EventSourcedEntity(tagKey = "...")` | Entity responds to events with matching tag |
| `@InjectEntity(idProperty = "...")` | Inject entity into command handler by tag |
| `EventAppender` | Explicit event publication (replaces static `AggregateLifecycle`) |

## Dependencies

- **Axon Framework:** 5.0.2 (production release)
- **Java:** 21+
- **Maven:** 3.8+

## Getting Started

```bash
# Clone repository
git clone https://github.com/maxzillabong/axon-5-dcb-demo
cd axon-5-dcb-demo

# Compile
mvn clean compile

# Run tests
mvn test
```

## Structure

```
src/main/java/nl/maxzilla/axon/
├── commands/          # Command messages
├── events/            # Event messages with @EventTag
├── traditional/       # Traditional Axon 4 aggregate pattern
└── dcb/              # Axon 5 DCB pattern
    ├── Account.java                      # Entity with @EventSourcedEntity
    └── TransferMoneyCommandHandler.java  # Handler with @InjectEntity
```

## Resources

- [Axon Framework 5 Getting Started](https://docs.axoniq.io/axon-framework-5-getting-started/)
- [Official University Demo](https://github.com/AxonIQ/university-demo)
- [DCB Documentation](https://docs.axoniq.io/axon-framework-reference/5.0/migration/solved-architecture-choices/)
- [Blog Post](https://maxzilla.nl/blog/axon-framework-5-dynamic-consistency-boundary)

## License

MIT License - See LICENSE file
