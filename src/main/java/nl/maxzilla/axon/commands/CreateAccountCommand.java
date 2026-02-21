package nl.maxzilla.axon.commands;

import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;

public record CreateAccountCommand(
    @TargetAggregateIdentifier String accountId,
    String accountType,
    BigDecimal initialBalance
) {}
