package com.clinical.dms.workflow;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface DoseOrderActivities {

    @ActivityMethod
    void allocateProductionBatch(String orderId);

    @ActivityMethod
    void computeDecayCompensation(String orderId);

    @ActivityMethod
    void recordAuditLog(String orderId, String action, String description);

    @ActivityMethod
    void notifyClinicalSite(String orderId, String message);
}
