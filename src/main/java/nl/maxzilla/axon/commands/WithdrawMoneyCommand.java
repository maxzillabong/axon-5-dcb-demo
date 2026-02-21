package nl.maxzilla.axon.commands;

import java.math.BigDecimal;

public record WithdrawMoneyCommand(
    String accountId,
    BigDecimal amount
) {}
