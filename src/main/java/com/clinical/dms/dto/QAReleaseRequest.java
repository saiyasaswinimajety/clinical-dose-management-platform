package com.clinical.dms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class QAReleaseRequest {

    @NotBlank(message = "QA Reviewer name is required")
    private String qaReviewerName;

    @NotBlank(message = "Electronic signature certification is required")
    private String electronicSignature;

    @NotBlank(message = "Regulatory release justification is required")
    private String releaseJustification;

    @Positive(message = "Assayed synthesized activity must be positive")
    private double assayedActivityMci;

    public QAReleaseRequest() {}

    public String getQaReviewerName() { return qaReviewerName; }
    public void setQaReviewerName(String qaReviewerName) { this.qaReviewerName = qaReviewerName; }

    public String getElectronicSignature() { return electronicSignature; }
    public void setElectronicSignature(String electronicSignature) { this.electronicSignature = electronicSignature; }

    public String getReleaseJustification() { return releaseJustification; }
    public void setReleaseJustification(String releaseJustification) { this.releaseJustification = releaseJustification; }

    public double getAssayedActivityMci() { return assayedActivityMci; }
    public void setAssayedActivityMci(double assayedActivityMci) { this.assayedActivityMci = assayedActivityMci; }
}
