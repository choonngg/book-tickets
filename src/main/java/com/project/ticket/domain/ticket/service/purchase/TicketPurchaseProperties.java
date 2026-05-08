package com.project.ticket.domain.ticket.service.purchase;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "ticket")
public class TicketPurchaseProperties {
    private PurchaseStrategy purchaseStrategy = PurchaseStrategy.DISTRIBUTED;

    public PurchaseStrategy getPurchaseStrategy() {
        return purchaseStrategy;
    }

    public void setPurchaseStrategy(PurchaseStrategy purchaseStrategy) {
        this.purchaseStrategy = purchaseStrategy;
    }
}
