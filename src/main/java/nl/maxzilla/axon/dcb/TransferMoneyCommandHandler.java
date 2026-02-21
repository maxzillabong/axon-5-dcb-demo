package nl.maxzilla.axon.dcb;

import nl.maxzilla.axon.commands.TransferMoneyCommand;
import nl.maxzilla.axon.events.MoneyTransferredEvent;
import org.axonframework.messaging.commandhandling.annotation.CommandHandler;
import org.axonframework.messaging.eventhandling.gateway.EventAppender;
import org.axonframework.modelling.annotation.InjectEntity;

public class TransferMoneyCommandHandler {
    
    @CommandHandler
    void handle(
            TransferMoneyCommand command,
            @InjectEntity(idProperty = "fromAccountId") Account fromAccount,
            @InjectEntity(idProperty = "toAccountId") Account toAccount,
            EventAppender eventAppender
    ) {
        // BOTH accounts are loaded into the consistency boundary
        // Business logic validation across BOTH entities
        fromAccount.withdraw(command.amount());
        toAccount.deposit(command.amount());
        
        // Publish event - atomic across both accounts
        eventAppender.append(
            new MoneyTransferredEvent(
                command.fromAccountId(),
                command.toAccountId(),
                command.transferId(),
                command.amount()
            )
        );
    }
}
