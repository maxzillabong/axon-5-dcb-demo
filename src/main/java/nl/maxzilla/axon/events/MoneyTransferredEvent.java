package nl.maxzilla.axon.events;

import java.math.BigDecimal;

public record MoneyTransferredEvent(
    String transferId,
    String fromAccountId,
    String toAccountId,
    BigDecimal amount
) {}
