package com.transport.TransportReportingSystem.controller;

import com.transport.TransportReportingSystem.dto.DashboardSummaryDTO;
import com.transport.TransportReportingSystem.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/summary")
    // @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'COMPANY_ADMIN')") // Handled in SecurityConfig but good to document
    public ResponseEntity<DashboardSummaryDTO> getSummary(java.security.Principal principal) {
        return ResponseEntity.ok(dashboardService.getSummary(principal));
    }
}
