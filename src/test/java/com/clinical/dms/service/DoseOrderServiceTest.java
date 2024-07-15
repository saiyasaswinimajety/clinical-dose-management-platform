package com.clinical.dms.service;

import com.clinical.dms.dto.CreateOrderRequest;
import com.clinical.dms.dto.OrderResponse;
import com.clinical.dms.dto.QAReleaseRequest;
import com.clinical.dms.model.*;
import com.clinical.dms.repository.ClinicalProtocolRepository;
import com.clinical.dms.repository.ClinicalSiteRepository;
import com.clinical.dms.repository.DoseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class DoseOrderServiceTest {

    private DoseOrderRepository orderRepository;
    private ClinicalProtocolRepository protocolRepository;
    private ClinicalSiteRepository siteRepository;
    private RadioactiveDecayService decayService;
    private AuditLedgerService auditService;
    private DoseOrderService doseOrderService;

    private Isotope f18;
    private ClinicalProtocol protocol;
    private ClinicalSite site;

    @BeforeEach
    void setUp() {
        orderRepository = mock(DoseOrderRepository.class);
        protocolRepository = mock(ClinicalProtocolRepository.class);
        siteRepository = mock(ClinicalSiteRepository.class);
        decayService = new RadioactiveDecayService();
        auditService = mock(AuditLedgerService.class);

        doseOrderService = new DoseOrderService(
                orderRepository, protocolRepository, siteRepository, decayService, auditService
        );

        f18 = new Isotope("ISO-F18", "F-18", "Fluorine-18", 109.77, "mCi");
        protocol = new ClinicalProtocol("PROT-ONC-001", "PROT-ONC-001", "Phase III Oncology Tau Imaging", "Eli Lilly", "PHASE_3", f18, 10.0);
        site = new ClinicalSite("SITE-JHU-01", "SITE-JHU-01", "Johns Hopkins Hospital PET Center", "Baltimore, MD", 39.296, -76.592, "pet@jhu.edu");
    }

    @Test
    @DisplayName("Should successfully create a dose order with precomputed decay compensation")
    void testCreateOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setProtocolCode("PROT-ONC-001");
        request.setSiteCode("SITE-JHU-01");
        request.setOrderedActivityMci(10.0);
        request.setRequestedDoseTime(Instant.now().plus(110, ChronoUnit.MINUTES));
        request.setPatientIdHash("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        request.setPerformedBy("clinical_coordinator@hospital.org");
        request.setReasonForChange("Initial Protocol Enrollment");

        when(protocolRepository.findByProtocolCode("PROT-ONC-001")).thenReturn(Optional.of(protocol));
        when(siteRepository.findBySiteCode("SITE-JHU-01")).thenReturn(Optional.of(site));
        when(orderRepository.save(any(DoseOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderResponse response = doseOrderService.createOrder(request, "10.0.0.1");

        assertNotNull(response);
        assertEquals("PROT-ONC-001", response.getProtocolCode());
        assertEquals("SITE-JHU-01", response.getSiteCode());
        assertEquals(10.0, response.getOrderedActivityMci());
        assertEquals(OrderStatus.ORDER_SUBMITTED, response.getStatus());
        assertTrue(response.getCompensatedActivityMci() > 19.0, "Decay compensation should roughly double after 1 half-life");

        verify(auditService, times(1)).recordAudit(
                eq("DOSE_ORDER"), any(), eq("INSERT"), isNull(), anyString(),
                eq("clinical_coordinator@hospital.org"), eq("Initial Protocol Enrollment"), eq("10.0.0.1")
        );
    }

    @Test
    @DisplayName("Should QA release order with 21 CFR Part 11 electronic signature audit")
    void testQAReleaseOrder() {
        DoseOrder existing = new DoseOrder("ORD-1", "DOSE-100", protocol, site, Instant.now().plus(1, ChronoUnit.HOURS), 10.0, "hash123");
        existing.setStatus(OrderStatus.RADIO_SYNTHESIS);

        when(orderRepository.findById("ORD-1")).thenReturn(Optional.of(existing));
        when(orderRepository.save(any(DoseOrder.class))).thenAnswer(inv -> inv.getArgument(0));

        QAReleaseRequest qaReq = new QAReleaseRequest();
        qaReq.setAssayedActivityMci(19.8);
        qaReq.setQaReviewerName("Dr. Marcus Vance, PharmD");
        qaReq.setReleaseJustification("Dose within USP <823> activity acceptance envelope (+-10%)");
        qaReq.setElectronicSignature("SIG_MVANCE_SECURE_AUTH_TOKEN_77218");

        OrderResponse res = doseOrderService.qaReleaseOrder("ORD-1", qaReq, "192.168.1.100");

        assertNotNull(res);
        assertEquals(OrderStatus.QA_RELEASED, res.getStatus());
        assertEquals(19.8, res.getSynthesizedActivityMci());

        verify(auditService, times(1)).recordAudit(
                eq("DOSE_ORDER"), eq("ORD-1"), eq("UPDATE"), eq("RADIO_SYNTHESIS"),
                contains("QA Released by Dr. Marcus Vance"), eq("Dr. Marcus Vance, PharmD"),
                eq("Dose within USP <823> activity acceptance envelope (+-10%)"), eq("192.168.1.100")
        );
    }
}
