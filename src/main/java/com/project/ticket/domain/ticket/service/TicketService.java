package com.project.ticket.domain.ticket.service;

import com.project.ticket.domain.ticket.dto.TicketPurchaseRequest;
import com.project.ticket.domain.ticket.dto.TicketPurchaseResponse;
import com.project.ticket.domain.ticket.service.purchase.TicketPurchaseStrategyRouter;
import com.project.ticket.global.exception.BusinessException;
import com.project.ticket.global.exception.ErrorCode;
import com.project.ticket.infra.idempotency.IdempotencyService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TicketService {
    private final IdempotencyService idempotencyService;
    private final TicketPurchaseStrategyRouter strategyRouter;

    public TicketService(
            IdempotencyService idempotencyService,
            TicketPurchaseStrategyRouter strategyRouter
    ) {
        this.idempotencyService = idempotencyService;
        this.strategyRouter = strategyRouter;
    }

    public TicketPurchaseResponse purchase(
            Long userId,
            TicketPurchaseRequest request,
            String idempotencyKey
    ) {
        if (!StringUtils.hasText(idempotencyKey)) {
            throw new BusinessException(ErrorCode.IDEMPOTENCY_KEY_REQUIRED);
        }
        return idempotencyService.findTicketPurchaseResponse(userId, idempotencyKey)
                .orElseGet(() -> purchaseNew(userId, request, idempotencyKey));
    }

    private TicketPurchaseResponse purchaseNew(Long userId, TicketPurchaseRequest request, String idempotencyKey) {
        TicketPurchaseResponse response = strategyRouter.current().purchase(userId, request.seatId());
        idempotencyService.saveTicketPurchaseResponse(userId, idempotencyKey, response);
        return response;
    }
}
