package nl.maxzilla.axon.dcb;

import nl.maxzilla.axon.commands.TransferMoneyCommand;
import nl.maxzilla.axon.events.MoneyTransferredEvent;

/**
 * CONCEPTUAL EXAMPLE of Dynamic Consistency Boundary in Axon Framework 5.
 * 
 * Note: This is not actual working code. The DCB API is experimental in Axon 5.0.0-M3
 * and subject to change. This shows the DESIGN INTENT.
 * 
 * KEY DIFFERENCE FROM TRADITIONAL AGGREGATES:
 * - Consistency boundary is defined PER OPERATION
 * - Can span multiple entities (fromAccount + toAccount)
 * - No saga required for simple cross-entity coordination
 * - Entities remain framework-agnostic (no Axon annotations on Account.java)
 */

// @DynamicConsistencyBoundary // <-- Conceptual annotation (not in current API)
public class TransferDCB {
    
    /**
     * This command handler defines a consistency boundary that includes BOTH accounts.
     * 
     * Traditional approach would require:
     * 1. Withdraw from source account (separate command)
     * 2. Deposit to target account (separate command)
     * 3. Saga to coordinate the two-phase operation
     * 4. Compensation logic if second operation fails
     * 
     * DCB approach:
     * - Consistency boundary spans both accounts FOR THIS OPERATION ONLY
     * - Both operations happen atomically within the boundary
     * - No saga orchestration needed
     * - Simpler, fewer moving parts
     */
    
    // @CommandHandler // <-- Conceptual (API may differ)
    public void handle(
        TransferMoneyCommand command
        // @Scope Account fromAccount,    // <-- Conceptual API
        // @Scope Account toAccount        // <-- Conceptual API
    ) {
        /*
         * Conceptual implementation:
         * 
         * // Load both accounts into the consistency boundary
         * fromAccount.withdraw(command.amount());
         * toAccount.deposit(command.amount());
         * 
         * // Emit event - consistency boundary ensures atomicity
         * apply(new MoneyTransferredEvent(
         *     command.transferId(),
         *     command.fromAccountId(),
         *     command.toAccountId(),
         *     command.amount()
         * ));
         * 
         * // If anything fails, entire operation rolls back
         * // No compensation logic needed
         * // No saga state management
         */
    }
    
    /**
     * Benefits of DCB approach:
     * 
     * 1. Simplicity:
     *    - No saga state management
     *    - No compensation logic
     *    - Single command, single operation
     * 
     * 2. Flexibility:
     *    - Boundary is operation-specific
     *    - Other operations can use different boundaries
     *    - Easy to add new operation types
     * 
     * 3. Evolution:
     *    - Account entity can change (add fields, rename)
     *    - No event migration required
     *    - Business logic adapts without architectural changes
     * 
     * 4. Clean domain model:
     *    - Account.java has zero Axon framework dependencies
     *    - Pure business logic
     *    - Easy to test, easy to understand
     */
    
    /**
     * Limitations:
     * 
     * 1. Not for distributed transactions:
     *    - DCB works within a bounded context
     *    - Cross-bounded-context operations still need sagas
     * 
     * 2. Not for temporal complexity:
     *    - If operation requires scheduling or time-based logic, use sagas
     * 
     * 3. Experimental API:
     *    - Subject to change in final 5.0.0 release
     *    - Not production-ready yet
     */
}
