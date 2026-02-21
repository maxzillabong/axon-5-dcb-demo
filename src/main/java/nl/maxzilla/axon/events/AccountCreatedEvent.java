package nl.maxzilla.axon.events;

import org.axonframework.eventsourcing.annotation.EventTag;
import java.math.BigDecimal;

public record AccountCreatedEvent(
    @EventTag(key = "accountId") String accountId,
    String accountType,
    BigDecimal initialBalance
) {}
