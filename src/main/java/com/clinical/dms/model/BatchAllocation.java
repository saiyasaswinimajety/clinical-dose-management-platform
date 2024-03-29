package com.clinical.dms.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "batch_allocations")
public class BatchAllocation {

    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "batch_number", nullable = false, unique = true, length = 64)
    private String batchNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private DoseOrder order;

    @Column(name = "synthesis_start_time", nullable = false)
    private Instant synthesisStartTime;

    @Column(name = "synthesis_end_time")
    private Instant synthesisEndTime;

    @Column(name = "initial_activity_mci", nullable = false)
    private double initialActivityMci;

    @Column(name = "qa_released_by", length = 64)
    private String qaReleasedBy;

    @Column(name = "qa_release_timestamp")
    private Instant qaReleaseTimestamp;

    @Column(name = "qa_notes", columnDefinition = "TEXT")
    private String qaNotes;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    public BatchAllocation() {}

    public BatchAllocation(String id, String batchNumber, DoseOrder order, Instant synthesisStartTime, double initialActivityMci) {
        this.id = id;
        this.batchNumber = batchNumber;
        this.order = order;
        this.synthesisStartTime = synthesisStartTime;
        this.initialActivityMci = initialActivityMci;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public DoseOrder getOrder() { return order; }
    public void setOrder(DoseOrder order) { this.order = order; }

    public Instant getSynthesisStartTime() { return synthesisStartTime; }
    public void setSynthesisStartTime(Instant synthesisStartTime) { this.synthesisStartTime = synthesisStartTime; }

    public Instant getSynthesisEndTime() { return synthesisEndTime; }
    public void setSynthesisEndTime(Instant synthesisEndTime) { this.synthesisEndTime = synthesisEndTime; }

    public double getInitialActivityMci() { return initialActivityMci; }
    public void setInitialActivityMci(double initialActivityMci) { this.initialActivityMci = initialActivityMci; }

    public String getQaReleasedBy() { return qaReleasedBy; }
    public void setQaReleasedBy(String qaReleasedBy) { this.qaReleasedBy = qaReleasedBy; }

    public Instant getQaReleaseTimestamp() { return qaReleaseTimestamp; }
    public void setQaReleaseTimestamp(Instant qaReleaseTimestamp) { this.qaReleaseTimestamp = qaReleaseTimestamp; }

    public String getQaNotes() { return qaNotes; }
    public void setQaNotes(String qaNotes) { this.qaNotes = qaNotes; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
