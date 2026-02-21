package nl.maxzilla.axon.traditional;

import nl.maxzilla.axon.commands.CreateAccountCommand;
import nl.maxzilla.axon.commands.DepositMoneyCommand;
import nl.maxzilla.axon.commands.WithdrawMoneyCommand;
import nl.maxzilla.axon.events.AccountCreatedEvent;
import nl.maxzilla.axon.events.MoneyDepositedEvent;
import nl.maxzilla.axon.events.MoneyWithdrawnEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import java.math.BigDecimal;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

/**
 * Traditional Axon 4-style aggregate.
 * 
 * PROBLEM: This aggregate definition is PERMANENT.
 * - Named "BankAccount" - what happens when business adds investment accounts?
 * - Single entity boundary - how do we handle cross-account transfers?
 * - Changing this requires event migration across all historical events
 */
@Aggregate
public class BankAccount {
    
    @AggregateIdentifier
    private String accountId;
    
    private String accountType;
    private BigDecimal balance;
    
    // Required by Axon
    protected BankAccount() {}
    
    @CommandHandler
    public BankAccount(CreateAccountCommand command) {
        // Validate
        if (command.initialBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        
        // Apply event
        apply(new AccountCreatedEvent(
            command.accountId(),
            command.accountType(),
            command.initialBalance()
        ));
    }
    
    @CommandHandler
    public void handle(DepositMoneyCommand command) {
        if (command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        
        apply(new MoneyDepositedEvent(command.accountId(), command.amount()));
    }
    
    @CommandHandler
    public void handle(WithdrawMoneyCommand command) {
        if (command.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        BigDecimal newBalance = this.balance.subtract(command.amount());
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        apply(new MoneyWithdrawnEvent(command.accountId(), command.amount()));
    }
    
    @EventSourcingHandler
    public void on(AccountCreatedEvent event) {
        this.accountId = event.accountId();
        this.accountType = event.accountType();
        this.balance = event.initialBalance();
    }
    
    @EventSourcingHandler
    public void on(MoneyDepositedEvent event) {
        this.balance = this.balance.add(event.amount());
    }
    
    @EventSourcingHandler
    public void on(MoneyWithdrawnEvent event) {
        this.balance = this.balance.subtract(event.amount());
    }
}
