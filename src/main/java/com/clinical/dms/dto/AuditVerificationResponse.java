package com.clinical.dms.dto;

import java.time.Instant;

public class AuditVerificationResponse {

    private boolean chainValid;
    private long totalRecordsAudited;
    private Long firstCorruptedRecordId;
    private String verificationSummary;
    private Instant verifiedAt;

    public AuditVerificationResponse() {}

    public AuditVerificationResponse(boolean chainValid, long totalRecordsAudited, Long firstCorruptedRecordId, String verificationSummary) {
        this.chainValid = chainValid;
        this.totalRecordsAudited = totalRecordsAudited;
        this.firstCorruptedRecordId = firstCorruptedRecordId;
        this.verificationSummary = verificationSummary;
        this.verifiedAt = Instant.now();
    }

    public boolean isChainValid() { return chainValid; }
    public boolean isIntegrityValid() { return chainValid; }
    public long getTotalRecordsAudited() { return totalRecordsAudited; }
    public long getTotalRecordsVerified() { return totalRecordsAudited; }
    public Long getFirstCorruptedRecordId() { return firstCorruptedRecordId; }
    public Long getCompromisedRecordId() { return firstCorruptedRecordId; }
    public String getVerificationSummary() { return verificationSummary; }
    public String getMessage() { return verificationSummary; }
    public Instant getVerifiedAt() { return verifiedAt; }
}
