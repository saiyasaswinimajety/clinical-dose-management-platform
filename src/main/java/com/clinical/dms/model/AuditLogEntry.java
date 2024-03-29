package com.clinical.dms.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "audit_logs")
public class AuditLogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entity_type", nullable = false, length = 64)
    private String entityType;

    @Column(name = "entity_id", nullable = false, length = 64)
    private String entityId;

    @Column(nullable = false, length = 16)
    private String action;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "performed_by", nullable = false, length = 128)
    private String performedBy;

    @Column(name = "performed_at", nullable = false)
    private Instant performedAt = Instant.now();

    @Column(name = "reason_for_change", length = 255)
    private String reasonForChange;

    @Column(name = "client_ip", length = 64)
    private String clientIp;

    @Column(name = "previous_hash", length = 64)
    private String previousHash;

    @Column(name = "record_hash", nullable = false, length = 64)
    private String recordHash;

    public AuditLogEntry() {}

    public AuditLogEntry(String entityType, String entityId, String action, String oldValue, String newValue,
                         String performedBy, String reasonForChange, String clientIp, String previousHash, String recordHash) {
        this.entityType = entityType;
        this.entityId = entityId;
        this.action = action;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.performedBy = performedBy;
        this.performedAt = Instant.now();
        this.reasonForChange = reasonForChange;
        this.clientIp = clientIp;
        this.previousHash = previousHash;
        this.recordHash = recordHash;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public Instant getPerformedAt() { return performedAt; }
    public void setPerformedAt(Instant performedAt) { this.performedAt = performedAt; }

    public String getReasonForChange() { return reasonForChange; }
    public void setReasonForChange(String reasonForChange) { this.reasonForChange = reasonForChange; }

    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }

    public String getPreviousHash() { return previousHash; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }

    public String getRecordHash() { return recordHash; }
    public void setRecordHash(String recordHash) { this.recordHash = recordHash; }
}
