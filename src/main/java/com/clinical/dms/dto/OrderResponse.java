package com.clinical.dms.dto;

import com.clinical.dms.model.OrderStatus;
import java.time.Instant;

public class OrderResponse {

    private String id;
    private String orderNumber;
    private String protocolCode;
    private String protocolTitle;
    private String siteCode;
    private String siteName;
    private String isotopeSymbol;
    private double halfLifeMinutes;
    private Instant requestedDoseTime;
    private double orderedActivityMci;
    private Double synthesizedActivityMci;
    private Double compensatedActivityMci;
    private OrderStatus status;
    private String patientIdHash;
    private Instant createdAt;

    public OrderResponse() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getProtocolCode() { return protocolCode; }
    public void setProtocolCode(String protocolCode) { this.protocolCode = protocolCode; }

    public String getProtocolTitle() { return protocolTitle; }
    public void setProtocolTitle(String protocolTitle) { this.protocolTitle = protocolTitle; }

    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public String getIsotopeSymbol() { return isotopeSymbol; }
    public void setIsotopeSymbol(String isotopeSymbol) { this.isotopeSymbol = isotopeSymbol; }

    public double getHalfLifeMinutes() { return halfLifeMinutes; }
    public void setHalfLifeMinutes(double halfLifeMinutes) { this.halfLifeMinutes = halfLifeMinutes; }

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
}
