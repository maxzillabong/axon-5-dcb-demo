package nl.maxzilla.axon.events;

import java.math.BigDecimal;

public record MoneyWithdrawnEvent(
    String accountId,
    BigDecimal amount
) {}
