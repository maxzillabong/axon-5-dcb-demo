package nl.maxzilla.axon.dcb;

import nl.maxzilla.axon.events.MoneyTransferredEvent;
import org.axonframework.eventsourcing.annotation.EventSourcingHandler;
import org.axonframework.eventsourcing.annotation.EventSourcedEntity;
import org.axonframework.eventsourcing.annotation.reflection.EntityCreator;

import java.math.BigDecimal;

@EventSourcedEntity(tagKey = "accountId")
public class Account {
    
    private String accountId;
    private BigDecimal balance = BigDecimal.ZERO;
    
    @EntityCreator
    public Account() {
    }
    
    @EventSourcingHandler
    void evolve(MoneyTransferredEvent event) {
        // This entity responds to events tagged with THIS account's ID
        // The event stream is filtered by the tagKey
        if (event.fromAccountId().equals(this.accountId)) {
            this.balance = this.balance.subtract(event.amount());
        } else if (event.toAccountId().equals(this.accountId)) {
            this.balance = this.balance.add(event.amount());
        }
    }
    
    public String getAccountId() {
        return accountId;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
    }
    
    public void deposit(BigDecimal amount) {
        // No validation needed for deposit
    }
}
