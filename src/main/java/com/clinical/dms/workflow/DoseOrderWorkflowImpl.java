package com.clinical.dms.workflow;

import io.temporal.activity.ActivityOptions;
import io.temporal.common.RetryOptions;
import io.temporal.workflow.Workflow;

import java.time.Duration;

public class DoseOrderWorkflowImpl implements DoseOrderWorkflow {

    private final ActivityOptions options = ActivityOptions.newBuilder()
            .setStartToCloseTimeout(Duration.ofMinutes(5))
            .setRetryOptions(RetryOptions.newBuilder()
                    .setMaximumAttempts(3)
                    .setBackoffCoefficient(2.0)
                    .build())
            .build();

    private final DoseOrderActivities activities =
            Workflow.newActivityStub(DoseOrderActivities.class, options);

    private boolean synthesisDone = false;
    private boolean qaReleased = false;
    private boolean dispatched = false;
    private double synthesizedActivity = 0.0;
    private String qaReviewer = null;

    @Override
    public void processDoseOrder(String orderId) {
        // Step 1: Batch Allocation
        activities.recordAuditLog(orderId, "WORKFLOW_INITIATED", "Temporal state machine started for dose order");
        activities.allocateProductionBatch(orderId);

        // Step 2: Await Radiopharmaceutical Synthesis Signal
        Workflow.await(Duration.ofHours(12), () -> synthesisDone);
        activities.recordAuditLog(orderId, "SYNTHESIS_SIGNALED", "Radiosynthesis completed with activity: " + synthesizedActivity + " mCi");

        // Step 3: Compute Half-Life Decay Compensation
        activities.computeDecayCompensation(orderId);

        // Step 4: Await Quality Assurance Electronic Release Signature
        Workflow.await(Duration.ofHours(6), () -> qaReleased);
        activities.recordAuditLog(orderId, "QA_RELEASE_SIGNALED", "QA Released by " + qaReviewer);

        // Step 5: Await Dispatch to Imaging Site
        Workflow.await(Duration.ofHours(24), () -> dispatched);
        activities.recordAuditLog(orderId, "DISPATCHED", "Dose order packaged in lead-shielded pig and dispatched");
        activities.notifyClinicalSite(orderId, "Your radioactive clinical trial dose has been dispatched.");
    }

    @Override
    public void signalSynthesisCompleted(double synthesizedActivityMci) {
        this.synthesizedActivity = synthesizedActivityMci;
        this.synthesisDone = true;
    }

    @Override
    public void signalQAReleased(String reviewerName, String reason) {
        this.qaReviewer = reviewerName;
        this.qaReleased = true;
    }

    @Override
    public void signalOrderDispatched(String trackingNumber) {
        this.dispatched = true;
    }
}
