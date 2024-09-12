package com.clinical.dms.controller;

import com.clinical.dms.dto.CreateOrderRequest;
import com.clinical.dms.dto.OrderResponse;
import com.clinical.dms.dto.QAReleaseRequest;
import com.clinical.dms.model.OrderStatus;
import com.clinical.dms.service.DoseOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DoseOrderController.class)
class DoseOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DoseOrderService doseOrderService;

    @Test
    @DisplayName("POST /api/v1/orders - should create order and return HTTP 201")
    void testCreateOrderEndpoint() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setProtocolCode("PROT-ONC-001");
        request.setSiteCode("SITE-JHU-01");
        request.setOrderedActivityMci(15.0);
        request.setRequestedDoseTime(Instant.now().plusSeconds(7200));
        request.setPatientIdHash("7b8979c65ebf68673a07de6de0d01d4a04d306b3bc4cfbfd885a539097d7c674");
        request.setPerformedBy("dr.ellis@oncology.jhu.edu");
        request.setReasonForChange("Clinical protocol initial enrollment dose");

        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setId("uuid-order-1");
        mockResponse.setOrderNumber("DOSE-1002938");
        mockResponse.setProtocolCode("PROT-ONC-001");
        mockResponse.setSiteCode("SITE-JHU-01");
        mockResponse.setOrderedActivityMci(15.0);
        mockResponse.setCompensatedActivityMci(32.4);
        mockResponse.setStatus(OrderStatus.ORDER_SUBMITTED);

        when(doseOrderService.createOrder(any(CreateOrderRequest.class), any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderNumber").value("DOSE-1002938"))
                .andExpect(jsonPath("$.status").value("ORDER_SUBMITTED"))
                .andExpect(jsonPath("$.orderedActivityMci").value(15.0))
                .andExpect(jsonPath("$.compensatedActivityMci").value(32.4));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{id} - should return order details")
    void testGetOrderById() throws Exception {
        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setId("uuid-order-1");
        mockResponse.setOrderNumber("DOSE-1002938");
        mockResponse.setStatus(OrderStatus.RADIO_SYNTHESIS);

        when(doseOrderService.getOrderById("uuid-order-1")).thenReturn(mockResponse);

        mockMvc.perform(get("/api/v1/orders/uuid-order-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("uuid-order-1"))
                .andExpect(jsonPath("$.status").value("RADIO_SYNTHESIS"));
    }

    @Test
    @DisplayName("POST /api/v1/orders/{id}/qa-release - should process QA release with 21 CFR Part 11 signature")
    void testQAReleaseEndpoint() throws Exception {
        QAReleaseRequest qaReq = new QAReleaseRequest();
        qaReq.setAssayedActivityMci(31.8);
        qaReq.setQaReviewerName("Dr. Marcus Vance, PharmD");
        qaReq.setReleaseJustification("Radiochemical purity >98%, endotoxin test passed");
        qaReq.setElectronicSignature("E-SIG-MVANCE-KEY-8849201");

        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setId("uuid-order-1");
        mockResponse.setStatus(OrderStatus.QA_RELEASED);
        mockResponse.setSynthesizedActivityMci(31.8);

        when(doseOrderService.qaReleaseOrder(eq("uuid-order-1"), any(QAReleaseRequest.class), any()))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api/v1/orders/uuid-order-1/qa-release")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(qaReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("QA_RELEASED"))
                .andExpect(jsonPath("$.synthesizedActivityMci").value(31.8));
    }
}
