package nl.maxzilla.axon.dcb;

import java.math.BigDecimal;

/**
 * Plain entity - no Axon annotations.
 * 
 * This is the DCB approach: domain models stay clean of framework concerns.
 * The consistency boundary is defined separately in TransferDCB.
 */
public class Account {
    
    private final String accountId;
    private final String accountType;
    private BigDecimal balance;
    
    public Account(String accountId, String accountType, BigDecimal initialBalance) {
        if (initialBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Initial balance cannot be negative");
        }
        
        this.accountId = accountId;
        this.accountType = accountType;
        this.balance = initialBalance;
    }
    
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
        this.balance = this.balance.add(amount);
    }
    
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
        
        BigDecimal newBalance = this.balance.subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        
        this.balance = newBalance;
    }
    
    public String getAccountId() {
        return accountId;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
}
