package nl.maxzilla.axon.commands;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferMoneyCommand(
    String transferId,
    String fromAccountId,
    String toAccountId,
    BigDecimal amount
) {
    public TransferMoneyCommand(String fromAccountId, String toAccountId, BigDecimal amount) {
        this(UUID.randomUUID().toString(), fromAccountId, toAccountId, amount);
    }
}
