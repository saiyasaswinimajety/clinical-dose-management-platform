package com.clinical.dms.service;

import com.clinical.dms.dto.CreateOrderRequest;
import com.clinical.dms.dto.OrderResponse;
import com.clinical.dms.dto.QAReleaseRequest;
import com.clinical.dms.model.*;
import com.clinical.dms.repository.ClinicalProtocolRepository;
import com.clinical.dms.repository.ClinicalSiteRepository;
import com.clinical.dms.repository.DoseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DoseOrderService {

    private final DoseOrderRepository orderRepository;
    private final ClinicalProtocolRepository protocolRepository;
    private final ClinicalSiteRepository siteRepository;
    private final RadioactiveDecayService decayService;
    private final AuditLedgerService auditService;

    public DoseOrderService(DoseOrderRepository orderRepository,
                            ClinicalProtocolRepository protocolRepository,
                            ClinicalSiteRepository siteRepository,
                            RadioactiveDecayService decayService,
                            AuditLedgerService auditService) {
        this.orderRepository = orderRepository;
        this.protocolRepository = protocolRepository;
        this.siteRepository = siteRepository;
        this.decayService = decayService;
        this.auditService = auditService;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request, String clientIp) {
        ClinicalProtocol protocol = protocolRepository.findByProtocolCode(request.getProtocolCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown protocol code: " + request.getProtocolCode()));

        ClinicalSite site = siteRepository.findBySiteCode(request.getSiteCode())
                .orElseThrow(() -> new IllegalArgumentException("Unknown site code: " + request.getSiteCode()));

        String orderId = UUID.randomUUID().toString();
        String orderNumber = "DOSE-" + System.currentTimeMillis();

        DoseOrder order = new DoseOrder(
                orderId,
                orderNumber,
                protocol,
                site,
                request.getRequestedDoseTime(),
                request.getOrderedActivityMci(),
                request.getPatientIdHash()
        );

        // Precompute initial decay compensation
        double elapsedMinutes = Math.max(0, Duration.between(Instant.now(), request.getRequestedDoseTime()).toMinutes());
        double compensated = decayService.calculateRequiredSynthesisActivity(
                request.getOrderedActivityMci(),
                protocol.getTargetIsotope().getHalfLifeMinutes(),
                elapsedMinutes
        );
        order.setCompensatedActivityMci(Math.round(compensated * 100.0) / 100.0);

        DoseOrder saved = orderRepository.save(order);

        // 21 CFR Part 11 Electronic Audit Log
        auditService.recordAudit(
                "DOSE_ORDER",
                saved.getId(),
                "INSERT",
                null,
                String.format("Created order %s for protocol %s with activity %f mCi", orderNumber, protocol.getProtocolCode(), request.getOrderedActivityMci()),
                request.getPerformedBy(),
                request.getReasonForChange(),
                clientIp
        );

        return mapToResponse(saved);
    }

    @Transactional
    public OrderResponse qaReleaseOrder(String orderId, QAReleaseRequest request, String clientIp) {
        DoseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        String oldStatus = order.getStatus().name();
        order.setSynthesizedActivityMci(request.getAssayedActivityMci());
        order.setStatus(OrderStatus.QA_RELEASED);
        order.setUpdatedAt(Instant.now());

        DoseOrder saved = orderRepository.save(order);

        // 21 CFR Part 11 Electronic Signature Log
        auditService.recordAudit(
                "DOSE_ORDER",
                saved.getId(),
                "UPDATE",
                oldStatus,
                String.format("QA Released by %s: Assayed = %f mCi. Signature: %s",
                        request.getQaReviewerName(), request.getAssayedActivityMci(), request.getElectronicSignature()),
                request.getQaReviewerName(),
                request.getReleaseJustification(),
                clientIp
        );

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String orderId) {
        DoseOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToResponse(DoseOrder order) {
        OrderResponse res = new OrderResponse();
        res.setId(order.getId());
        res.setOrderNumber(order.getOrderNumber());
        res.setProtocolCode(order.getProtocol().getProtocolCode());
        res.setProtocolTitle(order.getProtocol().getTitle());
        res.setSiteCode(order.getSite().getSiteCode());
        res.setSiteName(order.getSite().getName());
        res.setIsotopeSymbol(order.getProtocol().getTargetIsotope().getSymbol());
        res.setHalfLifeMinutes(order.getProtocol().getTargetIsotope().getHalfLifeMinutes());
        res.setRequestedDoseTime(order.getRequestedDoseTime());
        res.setOrderedActivityMci(order.getOrderedActivityMci());
        res.setSynthesizedActivityMci(order.getSynthesizedActivityMci());
        res.setCompensatedActivityMci(order.getCompensatedActivityMci());
        res.setStatus(order.getStatus());
        res.setPatientIdHash(order.getPatientIdHash());
        res.setCreatedAt(order.getCreatedAt());
        return res;
    }
}
