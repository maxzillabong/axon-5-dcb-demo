package nl.maxzilla.axon.events;

import java.math.BigDecimal;

public record MoneyDepositedEvent(
    String accountId,
    BigDecimal amount
) {}
