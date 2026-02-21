package nl.maxzilla.axon.traditional;

import nl.maxzilla.axon.commands.DepositMoneyCommand;
import nl.maxzilla.axon.commands.TransferMoneyCommand;
import nl.maxzilla.axon.commands.WithdrawMoneyCommand;
import nl.maxzilla.axon.events.MoneyDepositedEvent;
import nl.maxzilla.axon.events.MoneyTransferredEvent;
import nl.maxzilla.axon.events.MoneyWithdrawnEvent;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;

/**
 * Traditional saga for cross-account money transfers.
 * 
 * PROBLEM: This is heavyweight for what should be a simple operation.
 * 
 * Requirements:
 * - Withdraw from source account
 * - Deposit to target account
 * - Both operations must succeed or both must fail
 * 
 * Saga approach:
 * 1. Initiate transfer
 * 2. Send withdraw command
 * 3. Wait for withdraw event
 * 4. Send deposit command  
 * 5. Wait for deposit event
 * 6. Complete transfer
 * 7. Handle failures at each step
 * 8. Compensation logic if partial failure
 * 
 * This is 100+ lines of orchestration code for a conceptually simple operation.
 * 
 * DCB approach:
 * - Define consistency boundary that includes both accounts
 * - Execute both operations atomically
 * - ~20 lines of code
 */
@Saga
public class TransferSaga {
    
    private transient CommandGateway commandGateway;
    
    private String transferId;
    private String fromAccountId;
    private String toAccountId;
    private java.math.BigDecimal amount;
    
    private boolean withdrawCompleted = false;
    private boolean depositCompleted = false;
    
    @StartSaga
    @SagaEventHandler(associationProperty = "transferId")
    public void on(TransferMoneyCommand command) {
        this.transferId = command.transferId();
        this.fromAccountId = command.fromAccountId();
        this.toAccountId = command.toAccountId();
        this.amount = command.amount();
        
        // Step 1: Withdraw from source account
        commandGateway.send(new WithdrawMoneyCommand(
            command.fromAccountId(),
            command.amount()
        ));
    }
    
    @SagaEventHandler(associationProperty = "accountId", keyName = "fromAccountId")
    public void on(MoneyWithdrawnEvent event) {
        if (!withdrawCompleted && event.amount().equals(this.amount)) {
            this.withdrawCompleted = true;
            
            // Step 2: Deposit to target account
            commandGateway.send(new DepositMoneyCommand(
                this.toAccountId,
                this.amount
            ));
        }
    }
    
    @SagaEventHandler(associationProperty = "accountId", keyName = "toAccountId")
    @EndSaga
    public void on(MoneyDepositedEvent event) {
        if (withdrawCompleted && !depositCompleted && event.amount().equals(this.amount)) {
            this.depositCompleted = true;
            
            // Step 3: Transfer complete - emit final event
            // (In real implementation, you'd emit this via aggregate or process manager)
            // apply(new MoneyTransferredEvent(
            //     this.transferId,
            //     this.fromAccountId,
            //     this.toAccountId,
            //     this.amount
            // ));
        }
    }
    
    // Error handling (simplified - production needs comprehensive compensation)
    // What if withdraw succeeds but deposit fails?
    // - Need to reverse the withdrawal
    // - Need to handle "money in limbo" state
    // - Need idempotency checks
    // - Need timeout handling
    // This is where saga complexity explodes
}

/**
 * Lines of code comparison:
 * 
 * Saga approach:      ~120 lines (with proper error handling)
 * DCB approach:       ~20 lines
 * 
 * Cognitive complexity:
 * 
 * Saga:
 * - 3 events to correlate
 * - 2 association properties
 * - State management (withdrawCompleted, depositCompleted)
 * - Compensation logic
 * - Timeout handling
 * - Idempotency checks
 * 
 * DCB:
 * - 1 command handler
 * - Atomic operation
 * - No state management
 * - Framework handles failures
 * 
 * When to use Saga anyway:
 * - Cross-bounded-context operations
 * - Temporal complexity (delays, scheduling)
 * - Complex compensation flows
 * - Long-running processes
 * 
 * When DCB is better:
 * - Simple cross-entity operations within bounded context
 * - No temporal logic
 * - Straightforward success/failure semantics
 */
