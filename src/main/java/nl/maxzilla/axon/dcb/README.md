# Dynamic Consistency Boundary (DCB) - Conceptual Example

**Note:** Axon Framework 5.0.0 is still in milestone releases. The DCB API is experimental and subject to change.

The code in this directory represents the **conceptual design** of DCB based on:
- AxonIQ official blog posts
- Axon Framework 5 release notes  
- Community discussions

The exact API may differ in the final 5.0.0 release.

## Key Differences from Traditional Aggregates

### Traditional Aggregate (BankAccount.java)
- **Fixed boundary:** One aggregate = one entity
- **Permanent definition:** Change aggregate name = event migration nightmare
- **Cross-entity operations:** Requires heavyweight sagas

### Dynamic Consistency Boundary (TransferDCB.java)
- **Flexible boundary:** Spans multiple entities per operation
- **Evolution-friendly:** Aggregates can change without event migration
- **Simpler coordination:** No saga needed for simple cross-entity operations

## How DCB Works (Conceptually)

```java
// Traditional: Fixed boundary around single entity
@Aggregate
public class BankAccount {
    // This aggregate definition is PERMANENT
    // Changing it affects ALL historical events
}

// DCB: Boundary defined per operation
@DynamicConsistencyBoundary
public class TransferDCB {
    @CommandHandler
    public void handle(TransferMoneyCommand cmd,
                      @Scope Account fromAccount,
                      @Scope Account toAccount) {
        // Consistency boundary includes BOTH accounts
        // Only for THIS operation
        // Other operations can use different boundaries
    }
}
```

## When to Use DCB

**Use DCB when:**
- Operations naturally span multiple entities
- Business domain is evolving (aggregates need to change)
- You want to avoid saga overhead for simple coordination

**Stick with traditional aggregates when:**
- Domain is stable and well-understood
- Operations are clearly single-entity
- Simplicity > flexibility

## Implementation Status

- ✅ Conceptual design documented
- ⚠️ Experimental in Axon 5.0.0-M3
- ❌ Not production-ready yet
- 🔄 API subject to change

Check [Axon Framework 5 docs](https://docs.axoniq.io/axon-framework-reference/5.0/) for latest API.
