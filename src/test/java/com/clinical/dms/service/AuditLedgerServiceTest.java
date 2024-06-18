package com.clinical.dms.service;

import com.clinical.dms.dto.AuditVerificationResponse;
import com.clinical.dms.model.AuditLogEntry;
import com.clinical.dms.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuditLedgerServiceTest {

    private AuditLogRepository auditLogRepository;
    private AuditLedgerService auditLedgerService;
    private final String testSalt = "test-regulatory-salt-21cfr11";

    @BeforeEach
    void setUp() {
        auditLogRepository = mock(AuditLogRepository.class);
        auditLedgerService = new AuditLedgerService(auditLogRepository, testSalt);
    }

    @Test
    @DisplayName("Hash computation should be deterministic for identical input parameters")
    void testDeterministicHash() {
        Instant now = Instant.parse("2026-03-15T12:00:00Z");
        String hash1 = auditLedgerService.calculateHash("GENESIS", "DOSE_ORDER", "ORDER-1", "INSERT", "dr.smith", now, "payload");
        String hash2 = auditLedgerService.calculateHash("GENESIS", "DOSE_ORDER", "ORDER-1", "INSERT", "dr.smith", now, "payload");

        assertNotNull(hash1);
        assertEquals(64, hash1.length()); // SHA-256 hex string length
        assertEquals(hash1, hash2);
    }

    @Test
    @DisplayName("Hash must mutate if any field changes (avalanche effect)")
    void testHashAvalanche() {
        Instant now = Instant.parse("2026-03-15T12:00:00Z");
        String hashOriginal = auditLedgerService.calculateHash("GENESIS", "DOSE_ORDER", "ORDER-1", "INSERT", "dr.smith", now, "payload");
        String hashTampered = auditLedgerService.calculateHash("GENESIS", "DOSE_ORDER", "ORDER-1", "INSERT", "dr.smith", now, "payload_tampered");

        assertNotEquals(hashOriginal, hashTampered);
    }

    @Test
    @DisplayName("Recording first entry should link to GENESIS_HASH")
    void testRecordFirstAuditEntry() {
        when(auditLogRepository.findTopByOrderByIdDesc()).thenReturn(Optional.empty());
        when(auditLogRepository.save(any(AuditLogEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        AuditLogEntry entry = auditLedgerService.recordAudit(
                "DOSE_ORDER", "ORD-123", "INSERT",
                null, "Created Order ORD-123", "operator@lilly.com",
                "New Clinical Trial Dosing", "192.168.1.50"
        );

        assertEquals(AuditLedgerService.GENESIS_HASH, entry.getPreviousHash());
        assertNotNull(entry.getRecordHash());
        verify(auditLogRepository, times(1)).save(any(AuditLogEntry.class));
    }

    @Test
    @DisplayName("21 CFR Part 11 integrity verification should pass on intact audit chain")
    void testVerifyChainIntegrityPass() {
        List<AuditLogEntry> chain = new ArrayList<>();
        Instant t0 = Instant.parse("2026-01-01T10:00:00Z");
        Instant t1 = Instant.parse("2026-01-01T10:15:00Z");

        // Record 1
        String prev0 = AuditLedgerService.GENESIS_HASH;
        String hash0 = auditLedgerService.calculateHash(prev0, "DOSE_ORDER", "ORD-1", "INSERT", "user1", t0, "payload1");
        AuditLogEntry e0 = new AuditLogEntry("DOSE_ORDER", "ORD-1", "INSERT", null, "payload1", "user1", "reason1", "127.0.0.1", prev0, hash0);
        e0.setId(1L);
        e0.setPerformedAt(t0);
        chain.add(e0);

        // Record 2
        String prev1 = hash0;
        String hash1 = auditLedgerService.calculateHash(prev1, "DOSE_ORDER", "ORD-1", "UPDATE", "qa_user", t1, "payload2");
        AuditLogEntry e1 = new AuditLogEntry("DOSE_ORDER", "ORD-1", "UPDATE", "payload1", "payload2", "qa_user", "reason2", "127.0.0.1", prev1, hash1);
        e1.setId(2L);
        e1.setPerformedAt(t1);
        chain.add(e1);

        when(auditLogRepository.findAllByOrderByPerformedAtAsc()).thenReturn(chain);

        AuditVerificationResponse response = auditLedgerService.verifyChainIntegrity();

        assertTrue(response.isIntegrityValid());
        assertEquals(2, response.getTotalRecordsVerified());
        assertNull(response.getCompromisedRecordId());
    }

    @Test
    @DisplayName("21 CFR Part 11 integrity check should detect tampered payload and identify compromised record")
    void testVerifyChainDetectsTamper() {
        List<AuditLogEntry> chain = new ArrayList<>();
        Instant t0 = Instant.parse("2026-01-01T10:00:00Z");

        String prev0 = AuditLedgerService.GENESIS_HASH;
        String hash0 = auditLedgerService.calculateHash(prev0, "DOSE_ORDER", "ORD-1", "INSERT", "user1", t0, "valid_payload");
        
        // Maliciously altered payload in database
        AuditLogEntry e0 = new AuditLogEntry("DOSE_ORDER", "ORD-1", "INSERT", null, "TAMPERED_ILLEGAL_PAYLOAD", "user1", "reason1", "127.0.0.1", prev0, hash0);
        e0.setId(42L);
        e0.setPerformedAt(t0);
        chain.add(e0);

        when(auditLogRepository.findAllByOrderByPerformedAtAsc()).thenReturn(chain);

        AuditVerificationResponse response = auditLedgerService.verifyChainIntegrity();

        assertFalse(response.isIntegrityValid());
        assertEquals(42L, response.getCompromisedRecordId());
        assertTrue(response.getMessage().contains("Tamper detected at record ID 42"));
    }
}
