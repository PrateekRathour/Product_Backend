package com.productcatalog.controller;

import com.productcatalog.dto.ApiResponse;
import com.productcatalog.dto.inventory.InventoryAdjustRequestDto;
import com.productcatalog.dto.inventory.InventoryAuditLogDto;
import com.productcatalog.dto.inventory.InventoryResponseDto;
import com.productcatalog.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/v1/inventory")
@Tag(name = "Inventory", description = "Endpoints for stock adjustment, threshold management, and inventory auditing")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all inventory items (Manager / Admin)")
    public ResponseEntity<ApiResponse<List<InventoryResponseDto>>> getAllInventory() {
        List<InventoryResponseDto> list = inventoryService.getAllInventory();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get inventory for product ID (Manager / Admin)")
    public ResponseEntity<ApiResponse<InventoryResponseDto>> getInventoryByProductId(@PathVariable Long productId) {
        InventoryResponseDto inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success(inventory));
    }

    @PostMapping("/adjust/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Adjust inventory stock (Manager / Admin)")
    public ResponseEntity<ApiResponse<InventoryResponseDto>> adjustStock(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryAdjustRequestDto request,
            Authentication authentication) {
        String username = authentication != null ? authentication.getName() : "System Admin";
        InventoryResponseDto updated = inventoryService.adjustStock(productId, request, username);
        return ResponseEntity.ok(ApiResponse.success("Inventory adjusted successfully", updated));
    }

    @GetMapping("/low-stock-alerts")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get low stock and out-of-stock items (Manager / Admin)")
    public ResponseEntity<ApiResponse<List<InventoryResponseDto>>> getLowStockAlerts() {
        List<InventoryResponseDto> list = inventoryService.getLowStockAlerts();
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get inventory audit history logs (Manager / Admin)")
    public ResponseEntity<ApiResponse<List<InventoryAuditLogDto>>> getAuditLogs(
            @RequestParam(required = false) Long productId) {
        List<InventoryAuditLogDto> list = inventoryService.getAuditLogs(productId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }
}
