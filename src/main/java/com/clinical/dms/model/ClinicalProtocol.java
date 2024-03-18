package com.clinical.dms.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "clinical_protocols")
public class ClinicalProtocol {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "protocol_code", nullable = false, unique = true, length = 32)
    private String protocolCode;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 128)
    private String sponsor;

    @Column(nullable = false, length = 16)
    private String phase;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "target_isotope_id", nullable = false)
    private Isotope targetIsotope;

    @Column(name = "target_activity_mci", nullable = false)
    private double targetActivityMci;

    @Column(nullable = false, length = 32)
    private String status = "ACTIVE";

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public ClinicalProtocol() {}

    public ClinicalProtocol(String id, String protocolCode, String title, String sponsor, String phase,
                            Isotope targetIsotope, double targetActivityMci) {
        this.id = id;
        this.protocolCode = protocolCode;
        this.title = title;
        this.sponsor = sponsor;
        this.phase = phase;
        this.targetIsotope = targetIsotope;
        this.targetActivityMci = targetActivityMci;
        this.status = "ACTIVE";
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProtocolCode() { return protocolCode; }
    public void setProtocolCode(String protocolCode) { this.protocolCode = protocolCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSponsor() { return sponsor; }
    public void setSponsor(String sponsor) { this.sponsor = sponsor; }

    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }

    public Isotope getTargetIsotope() { return targetIsotope; }
    public void setTargetIsotope(Isotope targetIsotope) { this.targetIsotope = targetIsotope; }

    public double getTargetActivityMci() { return targetActivityMci; }
    public void setTargetActivityMci(double targetActivityMci) { this.targetActivityMci = targetActivityMci; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
