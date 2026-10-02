package com.productcatalog.service;

import com.productcatalog.dto.inventory.InventoryAdjustRequestDto;
import com.productcatalog.dto.inventory.InventoryAuditLogDto;
import com.productcatalog.dto.inventory.InventoryResponseDto;

import java.util.List;

public interface InventoryService {

    List<InventoryResponseDto> getAllInventory();

    InventoryResponseDto getInventoryByProductId(Long productId);

    InventoryResponseDto adjustStock(Long productId, InventoryAdjustRequestDto request, String username);

    List<InventoryResponseDto> getLowStockAlerts();

    List<InventoryAuditLogDto> getAuditLogs(Long productId);
}
