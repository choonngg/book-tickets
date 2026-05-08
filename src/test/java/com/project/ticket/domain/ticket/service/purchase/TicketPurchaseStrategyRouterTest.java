package com.project.ticket.domain.ticket.service.purchase;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TicketPurchaseStrategyRouterTest {

    private final TicketPurchaseStrategy optimistic = (userId, seatId) -> null;
    private final TicketPurchaseStrategy pessimistic = (userId, seatId) -> null;
    private final TicketPurchaseStrategy distributed = (userId, seatId) -> null;

    @Test
    void routesToOptimisticStrategy() {
        step("purchase-strategy 값을 OPTIMISTIC으로 설정한 router를 만든다.");
        TicketPurchaseStrategyRouter router = router(PurchaseStrategy.OPTIMISTIC);

        step("현재 전략이 낙관적 락 전략인지 검증한다.");
        assertThat(router.current()).isSameAs(optimistic);
    }

    @Test
    void routesToPessimisticStrategy() {
        step("purchase-strategy 값을 PESSIMISTIC으로 설정한 router를 만든다.");
        TicketPurchaseStrategyRouter router = router(PurchaseStrategy.PESSIMISTIC);

        step("현재 전략이 비관적 락 전략인지 검증한다.");
        assertThat(router.current()).isSameAs(pessimistic);
    }

    @Test
    void routesToDistributedStrategy() {
        step("purchase-strategy 값을 DISTRIBUTED로 설정한 router를 만든다.");
        TicketPurchaseStrategyRouter router = router(PurchaseStrategy.DISTRIBUTED);

        step("현재 전략이 분산락 전략인지 검증한다.");
        assertThat(router.current()).isSameAs(distributed);
    }

    private TicketPurchaseStrategyRouter router(PurchaseStrategy strategy) {
        TicketPurchaseProperties properties = new TicketPurchaseProperties();
        properties.setPurchaseStrategy(strategy);
        return new TicketPurchaseStrategyRouter(properties, optimistic, pessimistic, distributed);
    }

    private void step(String message) {
        System.out.println("[TEST STEP] " + message);
    }
}
