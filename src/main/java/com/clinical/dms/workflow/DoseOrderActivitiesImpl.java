package com.clinical.dms.workflow;

import com.clinical.dms.model.BatchAllocation;
import com.clinical.dms.model.DoseOrder;
import com.clinical.dms.model.OrderStatus;
import com.clinical.dms.repository.BatchAllocationRepository;
import com.clinical.dms.repository.DoseOrderRepository;
import com.clinical.dms.service.AuditLedgerService;
import com.clinical.dms.service.RadioactiveDecayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Component
public class DoseOrderActivitiesImpl implements DoseOrderActivities {

    private static final Logger log = LoggerFactory.getLogger(DoseOrderActivitiesImpl.class);

    private final DoseOrderRepository orderRepository;
    private final BatchAllocationRepository batchRepository;
    private final RadioactiveDecayService decayService;
    private final AuditLedgerService auditService;

    public DoseOrderActivitiesImpl(DoseOrderRepository orderRepository,
                                  BatchAllocationRepository batchRepository,
                                  RadioactiveDecayService decayService,
                                  AuditLedgerService auditService) {
        this.orderRepository = orderRepository;
        this.batchRepository = batchRepository;
        this.decayService = decayService;
        this.auditService = auditService;
    }

    @Override
    @Transactional
    public void allocateProductionBatch(String orderId) {
        DoseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        String batchNumber = "BATCH-" + System.currentTimeMillis() % 1000000;
        BatchAllocation allocation = new BatchAllocation(
                UUID.randomUUID().toString(),
                batchNumber,
                order,
                Instant.now(),
                order.getOrderedActivityMci() * 1.5 // Initial synthesis overage
        );
        batchRepository.save(allocation);

        order.setStatus(OrderStatus.BATCH_ALLOCATED);
        orderRepository.save(order);

        log.info("Production batch {} allocated for order {}", batchNumber, orderId);
    }

    @Override
    @Transactional
    public void computeDecayCompensation(String orderId) {
        DoseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        Instant now = Instant.now();
        double elapsedMinutes = Math.max(0, Duration.between(now, order.getRequestedDoseTime()).toMinutes());

        double requiredSynthesis = decayService.calculateRequiredSynthesisActivity(
                order.getOrderedActivityMci(),
                order.getProtocol().getTargetIsotope().getHalfLifeMinutes(),
                elapsedMinutes
        );

        order.setCompensatedActivityMci(Math.round(requiredSynthesis * 100.0) / 100.0);
        order.setStatus(OrderStatus.DECAY_COMPENSATED);
        orderRepository.save(order);

        log.info("Decay compensation computed for order {}: Target = {} mCi, Compensated Synthesis = {} mCi",
                orderId, order.getOrderedActivityMci(), order.getCompensatedActivityMci());
    }

    @Override
    public void recordAuditLog(String orderId, String action, String description) {
        auditService.recordAudit(
                "DOSE_ORDER_WORKFLOW",
                orderId,
                action,
                null,
                description,
                "TemporalWorkflowEngine",
                description,
                "127.0.0.1"
        );
    }

    @Override
    public void notifyClinicalSite(String orderId, String message) {
        log.info("[NOTIFICATION DISPATCH] Order ID: {} -> Site Alert: {}", orderId, message);
    }
}
