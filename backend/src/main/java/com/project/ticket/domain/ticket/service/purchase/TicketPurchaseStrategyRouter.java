package com.project.ticket.domain.ticket.service.purchase;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class TicketPurchaseStrategyRouter {
    private final TicketPurchaseProperties properties;
    private final TicketPurchaseStrategy optimistic;
    private final TicketPurchaseStrategy pessimistic;
    private final TicketPurchaseStrategy distributed;

    public TicketPurchaseStrategyRouter(
            TicketPurchaseProperties properties,
            @Qualifier("optimisticTicketPurchaseStrategy") TicketPurchaseStrategy optimistic,
            @Qualifier("pessimisticTicketPurchaseStrategy") TicketPurchaseStrategy pessimistic,
            @Qualifier("distributedTicketPurchaseStrategy") TicketPurchaseStrategy distributed
    ) {
        this.properties = properties;
        this.optimistic = optimistic;
        this.pessimistic = pessimistic;
        this.distributed = distributed;
    }

    public TicketPurchaseStrategy current() {
        return switch (properties.getPurchaseStrategy()) {
            case OPTIMISTIC -> optimistic;
            case PESSIMISTIC -> pessimistic;
            case DISTRIBUTED -> distributed;
        };
    }
}
