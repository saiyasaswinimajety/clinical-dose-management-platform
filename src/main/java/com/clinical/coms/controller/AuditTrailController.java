package com.clinical.coms.controller;

import com.clinical.coms.dto.AuditVerificationResponse;
import com.clinical.coms.model.AuditLogEntry;
import com.clinical.coms.service.AuditLedgerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-trail")
public class AuditTrailController {

    private final AuditLedgerService auditLedgerService;

    public AuditTrailController(AuditLedgerService auditLedgerService) {
        this.auditLedgerService = auditLedgerService;
    }

    @GetMapping("/verify")
    public ResponseEntity<AuditVerificationResponse> verifyIntegrity() {
        return ResponseEntity.ok(auditLedgerService.verifyChainIntegrity());
    }

    @GetMapping
    public ResponseEntity<List<AuditLogEntry>> listAuditLogs() {
        return ResponseEntity.ok(auditLedgerService.listAuditLogs());
    }
}
