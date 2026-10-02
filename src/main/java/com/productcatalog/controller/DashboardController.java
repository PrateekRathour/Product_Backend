package com.productcatalog.controller;

import com.productcatalog.dto.ApiResponse;
import com.productcatalog.dto.dashboard.DashboardStatsDto;
import com.productcatalog.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Endpoints for catalog analytics and metrics overview")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get detailed dashboard stats and category valuations (Manager / Admin)")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        DashboardStatsDto stats = dashboardService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @GetMapping("/public-stats")
    @Operation(summary = "Get high-level public catalog numbers (Public)")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getPublicStats() {
        DashboardStatsDto stats = dashboardService.getPublicStats();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
}
