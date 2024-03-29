package com.clinical.dms.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "dose_orders")
public class DoseOrder {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "order_number", nullable = false, unique = true, length = 64)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "protocol_id", nullable = false)
    private ClinicalProtocol protocol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "site_id", nullable = false)
    private ClinicalSite site;

    @Column(name = "requested_dose_time", nullable = false)
    private Instant requestedDoseTime;

    @Column(name = "ordered_activity_mci", nullable = false)
    private double orderedActivityMci;

    @Column(name = "synthesized_activity_mci")
    private Double synthesizedActivityMci;

    @Column(name = "compensated_activity_mci")
    private Double compensatedActivityMci;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private OrderStatus status = OrderStatus.ORDER_SUBMITTED;

    @Column(name = "patient_id_hash", nullable = false, length = 64)
    private String patientIdHash;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public DoseOrder() {}

    public DoseOrder(String id, String orderNumber, ClinicalProtocol protocol, ClinicalSite site,
                     Instant requestedDoseTime, double orderedActivityMci, String patientIdHash) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.protocol = protocol;
        this.site = site;
        this.requestedDoseTime = requestedDoseTime;
        this.orderedActivityMci = orderedActivityMci;
        this.patientIdHash = patientIdHash;
        this.status = OrderStatus.ORDER_SUBMITTED;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public ClinicalProtocol getProtocol() { return protocol; }
    public void setProtocol(ClinicalProtocol protocol) { this.protocol = protocol; }

    public ClinicalSite getSite() { return site; }
    public void setSite(ClinicalSite site) { this.site = site; }

    public Instant getRequestedDoseTime() { return requestedDoseTime; }
    public void setRequestedDoseTime(Instant requestedDoseTime) { this.requestedDoseTime = requestedDoseTime; }

    public double getOrderedActivityMci() { return orderedActivityMci; }
    public void setOrderedActivityMci(double orderedActivityMci) { this.orderedActivityMci = orderedActivityMci; }

    public Double getSynthesizedActivityMci() { return synthesizedActivityMci; }
    public void setSynthesizedActivityMci(Double synthesizedActivityMci) { this.synthesizedActivityMci = synthesizedActivityMci; }

    public Double getCompensatedActivityMci() { return compensatedActivityMci; }
    public void setCompensatedActivityMci(Double compensatedActivityMci) { this.compensatedActivityMci = compensatedActivityMci; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public String getPatientIdHash() { return patientIdHash; }
    public void setPatientIdHash(String patientIdHash) { this.patientIdHash = patientIdHash; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
