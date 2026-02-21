package nl.maxzilla.axon.events;

import org.axonframework.eventsourcing.annotation.EventTag;
import java.math.BigDecimal;

public record MoneyTransferredEvent(
    @EventTag(key = "fromAccountId") String fromAccountId,
    @EventTag(key = "toAccountId") String toAccountId,
    String transferId,
    BigDecimal amount
) {}
