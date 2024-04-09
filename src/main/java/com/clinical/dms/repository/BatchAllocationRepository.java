package com.clinical.dms.repository;

import com.clinical.dms.model.BatchAllocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchAllocationRepository extends JpaRepository<BatchAllocation, String> {
    Optional<BatchAllocation> findByBatchNumber(String batchNumber);
    List<BatchAllocation> findByOrderId(String orderId);
}
