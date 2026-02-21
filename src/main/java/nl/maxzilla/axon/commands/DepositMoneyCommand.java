package nl.maxzilla.axon.commands;

import java.math.BigDecimal;

public record DepositMoneyCommand(
    String accountId,
    BigDecimal amount
) {}
