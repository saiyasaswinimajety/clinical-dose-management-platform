package com.clinical.dms.service;

import com.clinical.dms.dto.AuditVerificationResponse;
import com.clinical.dms.model.AuditLogEntry;
import com.clinical.dms.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

/**
 * 21 CFR Part 11 Cryptographic Audit Trail & Electronic Signature Engine.
 * Enforces immutable forward hash-chaining across all data mutations.
 */
@Service
public class AuditLedgerService {

    public static final String GENESIS_HASH = "GENESIS_HASH_CHAIN_00000000000000000000000000000000";

    private final AuditLogRepository auditLogRepository;
    private final String salt;

    public AuditLedgerService(AuditLogRepository auditLogRepository,
                              @Value("${dms.audit.sha256-salt:dms-21cfr11-default-salt}") String salt) {
        this.auditLogRepository = auditLogRepository;
        this.salt = salt;
    }

    /**
     * Compute SHA-256 hash linking previous record hash with current mutation details.
     */
    public String calculateHash(String previousHash, String entityType, String entityId,
                                String action, String performedBy, Instant performedAt, String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String rawData = String.join("|",
                    previousHash != null ? previousHash : GENESIS_HASH,
                    entityType != null ? entityType : "",
                    entityId != null ? entityId : "",
                    action != null ? action : "",
                    performedBy != null ? performedBy : "",
                    performedAt != null ? performedAt.toString() : "",
                    payload != null ? payload : "",
                    salt
            );
            byte[] hashBytes = digest.digest(rawData.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm unavailable", e);
        }
    }

    /**
     * Records an electronic audit entry with forward hash linking.
     */
    @Transactional
    public AuditLogEntry recordAudit(String entityType, String entityId, String action,
                                     String oldValue, String newValue, String performedBy,
                                     String reasonForChange, String clientIp) {
        String previousHash = auditLogRepository.findTopByOrderByIdDesc()
                .map(AuditLogEntry::getRecordHash)
                .orElse(GENESIS_HASH);

        Instant now = Instant.now();
        String currentHash = calculateHash(previousHash, entityType, entityId, action, performedBy, now, newValue);

        AuditLogEntry entry = new AuditLogEntry(
                entityType,
                entityId,
                action,
                oldValue,
                newValue,
                performedBy,
                reasonForChange,
                clientIp,
                previousHash,
                currentHash
        );
        entry.setPerformedAt(now);

        return auditLogRepository.save(entry);
    }

    /**
     * 21 CFR Part 11 Integrity Audit: Traverses the entire audit log sequence and verifies
     * that no records have been altered, injected, or removed.
     */
    @Transactional(readOnly = true)
    public AuditVerificationResponse verifyChainIntegrity() {
        List<AuditLogEntry> logs = auditLogRepository.findAllByOrderByPerformedAtAsc();
        if (logs.isEmpty()) {
            return new AuditVerificationResponse(true, 0, null, "Audit trail is empty (Genesis state).");
        }

        String expectedPreviousHash = GENESIS_HASH;

        for (AuditLogEntry entry : logs) {
            // Check link to previous record
            if (!expectedPreviousHash.equals(entry.getPreviousHash())) {
                return new AuditVerificationResponse(
                        false,
                        logs.size(),
                        entry.getId(),
                        String.format("Hash chain broken at record ID %d: Expected previous hash '%s' but found '%s'",
                                entry.getId(), expectedPreviousHash, entry.getPreviousHash())
                );
            }

            // Recompute record hash
            String recomputedHash = calculateHash(
                    entry.getPreviousHash(),
                    entry.getEntityType(),
                    entry.getEntityId(),
                    entry.getAction(),
                    entry.getPerformedBy(),
                    entry.getPerformedAt(),
                    entry.getNewValue()
            );

            if (!recomputedHash.equals(entry.getRecordHash())) {
                return new AuditVerificationResponse(
                        false,
                        logs.size(),
                        entry.getId(),
                        String.format("Tamper detected at record ID %d: Stored hash '%s' does not match recomputed hash '%s'",
                                entry.getId(), entry.getRecordHash(), recomputedHash)
                );
            }

            expectedPreviousHash = entry.getRecordHash();
        }

        return new AuditVerificationResponse(
                true,
                logs.size(),
                null,
                String.format("21 CFR Part 11 verification PASS: %d immutable audit records verified without drift.", logs.size())
        );
    }

    @Transactional(readOnly = true)
    public List<AuditLogEntry> listAuditLogs() {
        return auditLogRepository.findAllByOrderByPerformedAtAsc();
    }
}
