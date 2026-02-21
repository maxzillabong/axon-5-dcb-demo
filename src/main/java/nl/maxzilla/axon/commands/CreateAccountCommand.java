package nl.maxzilla.axon.commands;

import java.math.BigDecimal;

public record CreateAccountCommand(
    String accountId,
    String accountType,
    BigDecimal initialBalance
) {}
