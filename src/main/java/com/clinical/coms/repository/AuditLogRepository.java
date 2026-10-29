package com.clinical.coms.repository;

import com.clinical.coms.model.AuditLogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Long> {
    List<AuditLogEntry> findByEntityTypeAndEntityIdOrderByPerformedAtAsc(String entityType, String entityId);
    List<AuditLogEntry> findAllByOrderByPerformedAtAsc();
    Optional<AuditLogEntry> findTopByOrderByIdDesc();
}
