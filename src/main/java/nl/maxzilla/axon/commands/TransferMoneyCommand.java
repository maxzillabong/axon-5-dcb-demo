package nl.maxzilla.axon.commands;

import java.math.BigDecimal;

public record TransferMoneyCommand(
    String transferId,
    String fromAccountId,
    String toAccountId,
    BigDecimal amount
) {}
