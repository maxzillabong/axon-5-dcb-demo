# Traditional Aggregates vs Dynamic Consistency Boundary

## Side-by-Side Comparison

### Scenario: Cross-Account Money Transfer

Transfer €100 from Account A to Account B.

---

## Traditional Approach (Axon 4 style)

### Architecture
```
TransferMoneyCommand
    ↓
TransferSaga (orchestrator)
    ↓
WithdrawMoneyCommand → BankAccount (Account A)
    ↓
MoneyWithdrawnEvent
    ↓
DepositMoneyCommand → BankAccount (Account B)
    ↓
MoneyDepositedEvent
    ↓
Transfer Complete
```

### Code Complexity
- **Files:** 3 (BankAccount aggregate, TransferSaga, events)
- **Lines of code:** ~120 lines
- **Axon components:** Aggregate, Saga, CommandGateway
- **State management:** Saga state (withdrawCompleted, depositCompleted, transferId)

### What Can Go Wrong
1. Withdraw succeeds, deposit fails → money in limbo
2. Timeout between operations → need compensation
3. Duplicate events → need idempotency checks
4. Saga state corruption → manual intervention

### When It Makes Sense
- Cross-bounded-context operations
- Temporal logic (delays, scheduling)
- Complex compensation flows
- Long-running processes

---

## DCB Approach (Axon 5)

### Architecture (Conceptual)
```
TransferMoneyCommand
    ↓
TransferDCB (consistency boundary spans both accounts)
    ↓
Account A.withdraw() + Account B.deposit() [atomic]
    ↓
MoneyTransferredEvent
    ↓
Transfer Complete
```

### Code Complexity
- **Files:** 2 (Account entity, TransferDCB boundary)
- **Lines of code:** ~20 lines
- **Axon components:** DynamicConsistencyBoundary
- **State management:** None (operation is atomic)

### What Can Go Wrong
1. Insufficient funds → operation fails, nothing persisted
2. Invalid amount → operation fails, nothing persisted
3. Framework error → operation rolls back cleanly

### When It Makes Sense
- Cross-entity operations within bounded context
- No temporal complexity
- Simple success/failure semantics
- Evolving business domains

---

## The Real Difference: Evolution Over Time

### Year 1: Simple Banking

**Traditional:**
```java
@Aggregate
public class BankAccount {
    private String accountId;
    private BigDecimal balance;
}
```

**DCB:**
```java
public class Account {
    private String accountId;
    private BigDecimal balance;
}
```

**Verdict:** No significant difference yet.

---

### Year 2: Business Adds Investment Accounts

**Traditional:**
- **Option 1:** Rename `BankAccount` → `FinancialAccount`
  - Requires event migration for all historical events
  - Risky, time-consuming
- **Option 2:** Create separate `InvestmentAccount` aggregate
  - Duplicates logic
  - Cross-account transfers require saga anyway
- **Option 3:** Use inheritance
  - Aggregate inheritance is painful in Axon

**DCB:**
```java
public class Account {
    private String accountId;
    private String accountType; // "CHECKING", "SAVINGS", "INVESTMENT"
    private BigDecimal balance;
}
```
- Add `accountType` field
- No event migration needed (DCB boundaries adapt)
- Transfer logic stays the same

**Verdict:** DCB wins on evolution flexibility.

---

### Year 3: Business Adds Loan Products

**Traditional:**
- Loan repayment affects both loan account AND checking account
- Need another saga for loan payments
- Complexity compounds with each new product type

**DCB:**
```java
@DynamicConsistencyBoundary
public class LoanPaymentDCB {
    @CommandHandler
    public void handle(PayLoanCommand cmd,
                      @Scope LoanAccount loan,
                      @Scope Account checkingAccount) {
        loan.recordPayment(cmd.amount());
        checkingAccount.withdraw(cmd.amount());
    }
}
```
- Add new boundary definition
- Existing accounts unchanged
- No migration, no refactoring

**Verdict:** DCB scales better as domain complexity grows.

---

## Metrics Summary

| Metric | Traditional | DCB | Winner |
|--------|------------|-----|--------|
| Initial complexity | Low | Medium | Traditional |
| Lines of code (simple transfer) | 120 | 20 | DCB |
| Cross-entity operations | Saga required | Boundary definition | DCB |
| Aggregate evolution | Event migration | Adapt boundaries | DCB |
| Learning curve | Well-documented | Experimental | Traditional |
| Production readiness | ✅ | ⚠️ (M3) | Traditional |
| Scales with domain complexity | Poor | Good | DCB |

---

## Honest Verdict

### Use Traditional Aggregates When:
- ✅ Domain is stable and well-understood
- ✅ Operations are clearly single-entity
- ✅ Saga orchestration is needed anyway (temporal logic)
- ✅ You need battle-tested, production-ready code

### Use DCB When:
- ✅ Domain is evolving (common in startups/growth companies)
- ✅ Operations naturally span multiple entities
- ✅ Avoiding saga overhead is important
- ✅ You're okay with experimental APIs
- ✅ Axon Server 2025.1+ is available

### The Middle Ground:
Start with traditional aggregates. When you hit the evolution wall, migrate to DCB for specific operations that need it. You don't have to use DCB everywhere—it's opt-in.

---

## For the Blog Post

**Key message:** DCB doesn't replace traditional aggregates—it complements them. Use DCB when aggregate rigidity becomes a problem, not as a default choice.

**Story arc:**
1. Started with traditional aggregates (worked great)
2. Business evolved faster than architecture
3. Hit the aggregate evolution wall
4. Discovered DCB
5. Migrated specific operations
6. Kept traditional aggregates for stable parts

**Technical proof:**
- Show code comparison (this file)
- Show LOC metrics (120 vs 20)
- Show evolution timeline (Year 1-3)
- Link to working repo

**Honest limitations:**
- DCB is experimental (M3 milestone)
- API may change before 5.0.0 final
- Learning curve for new concept
- Not for distributed transactions
- Not for temporal complexity

**Verdict:**
If I had DCB in 2023, would I have stuck with Event Sourcing for that financial platform? Maybe. The aggregate rigidity problem was real. DCB addresses it. Event Sourcing is still not always the answer, but now it's viable for more use cases.
