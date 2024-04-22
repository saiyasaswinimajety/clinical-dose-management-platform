package com.clinical.dms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Instant;

public class CreateOrderRequest {

    @NotBlank(message = "Protocol code is required")
    private String protocolCode;

    @NotBlank(message = "Site code is required")
    private String siteCode;

    @NotNull(message = "Requested dose time is required")
    private Instant requestedDoseTime;

    @Positive(message = "Ordered activity must be greater than zero")
    private double orderedActivityMci;

    @NotBlank(message = "Patient ID hash is required (anonymized for 21 CFR Part 11)")
    private String patientIdHash;

    @NotBlank(message = "Performed by (electronic signature) is required")
    private String performedBy;

    private String reasonForChange = "Initial Dose Order Creation";

    public CreateOrderRequest() {}

    public String getProtocolCode() { return protocolCode; }
    public void setProtocolCode(String protocolCode) { this.protocolCode = protocolCode; }

    public String getSiteCode() { return siteCode; }
    public void setSiteCode(String siteCode) { this.siteCode = siteCode; }

    public Instant getRequestedDoseTime() { return requestedDoseTime; }
    public void setRequestedDoseTime(Instant requestedDoseTime) { this.requestedDoseTime = requestedDoseTime; }

    public double getOrderedActivityMci() { return orderedActivityMci; }
    public void setOrderedActivityMci(double orderedActivityMci) { this.orderedActivityMci = orderedActivityMci; }

    public String getPatientIdHash() { return patientIdHash; }
    public void setPatientIdHash(String patientIdHash) { this.patientIdHash = patientIdHash; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getReasonForChange() { return reasonForChange; }
    public void setReasonForChange(String reasonForChange) { this.reasonForChange = reasonForChange; }
}
