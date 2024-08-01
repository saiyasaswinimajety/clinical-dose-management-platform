package com.clinical.dms.workflow;

import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface DoseOrderWorkflow {

    @WorkflowMethod
    void processDoseOrder(String orderId);

    @SignalMethod
    void signalSynthesisCompleted(double synthesizedActivityMci);

    @SignalMethod
    void signalQAReleased(String reviewerName, String reason);

    @SignalMethod
    void signalOrderDispatched(String trackingNumber);
}
