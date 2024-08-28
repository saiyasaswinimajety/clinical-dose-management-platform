package com.clinical.dms.controller;

import com.clinical.dms.dto.CreateOrderRequest;
import com.clinical.dms.dto.OrderResponse;
import com.clinical.dms.dto.QAReleaseRequest;
import com.clinical.dms.service.DoseOrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class DoseOrderController {

    private final DoseOrderService doseOrderService;

    public DoseOrderController(DoseOrderService doseOrderService) {
        this.doseOrderService = doseOrderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                                     HttpServletRequest httpRequest) {
        String clientIp = resolveClientIp(httpRequest);
        OrderResponse response = doseOrderService.createOrder(request, clientIp);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id) {
        return ResponseEntity.ok(doseOrderService.getOrderById(id));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> listOrders() {
        return ResponseEntity.ok(doseOrderService.listOrders());
    }

    @PostMapping("/{id}/qa-release")
    public ResponseEntity<OrderResponse> qaReleaseOrder(@PathVariable String id,
                                                        @Valid @RequestBody QAReleaseRequest request,
                                                        HttpServletRequest httpRequest) {
        String clientIp = resolveClientIp(httpRequest);
        OrderResponse response = doseOrderService.qaReleaseOrder(id, request, clientIp);
        return ResponseEntity.ok(response);
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
