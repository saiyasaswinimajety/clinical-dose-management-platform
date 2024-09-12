package com.clinical.dms.controller;

import com.clinical.dms.dto.AuditVerificationResponse;
import com.clinical.dms.service.AuditLedgerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuditTrailController.class)
class AuditTrailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuditLedgerService auditLedgerService;

    @Test
    @DisplayName("GET /api/v1/audit-trail/verify - should return verification pass")
    void testVerifyAuditTrailEndpoint() throws Exception {
        AuditVerificationResponse mockResponse = new AuditVerificationResponse(
                true,
                142,
                null,
                "21 CFR Part 11 verification PASS: 142 immutable audit records verified without drift."
        );

        when(auditLedgerService.verifyChainIntegrity()).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/audit-trail/verify"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.integrityValid").value(true))
                .andExpect(jsonPath("$.totalRecordsVerified").value(142))
                .andExpect(jsonPath("$.message").value("21 CFR Part 11 verification PASS: 142 immutable audit records verified without drift."));
    }
}
