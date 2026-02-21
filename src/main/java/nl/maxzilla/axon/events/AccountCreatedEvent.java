package nl.maxzilla.axon.events;

import java.math.BigDecimal;

public record AccountCreatedEvent(
    String accountId,
    String accountType,
    BigDecimal initialBalance
) {}
