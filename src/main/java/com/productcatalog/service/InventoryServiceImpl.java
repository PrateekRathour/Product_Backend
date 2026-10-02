package com.productcatalog.service;

import com.productcatalog.dto.inventory.InventoryAdjustRequestDto;
import com.productcatalog.dto.inventory.InventoryAuditLogDto;
import com.productcatalog.dto.inventory.InventoryResponseDto;
import com.productcatalog.exception.BadRequestException;
import com.productcatalog.exception.ResourceNotFoundException;
import com.productcatalog.model.Inventory;
import com.productcatalog.model.InventoryAuditLog;
import com.productcatalog.model.Product;
import com.productcatalog.repository.InventoryAuditLogRepository;
import com.productcatalog.repository.InventoryRepository;
import com.productcatalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryAuditLogRepository auditLogRepository;

    private InventoryResponseDto mapToDto(Inventory inventory) {
        InventoryResponseDto dto = new InventoryResponseDto();
        dto.setId(inventory.getId());
        dto.setSku(inventory.getSku());
        dto.setStockQuantity(inventory.getStockQuantity());
        dto.setReservedQuantity(inventory.getReservedQuantity());
        dto.setAvailableQuantity(inventory.getStockQuantity() - inventory.getReservedQuantity());
        dto.setLowStockThreshold(inventory.getLowStockThreshold());
        dto.setStatus(inventory.getStatus());
        dto.setWarehouseLocation(inventory.getWarehouseLocation());
        dto.setLastRestockedAt(inventory.getLastRestockedAt());
        dto.setUpdatedAt(inventory.getUpdatedAt());
        return dto;
    }

    private InventoryAuditLogDto mapToAuditDto(InventoryAuditLog log) {
        InventoryAuditLogDto dto = new InventoryAuditLogDto();
        dto.setId(log.getId());
        dto.setProductId(log.getProductId());
        dto.setProductName(log.getProductName());
        dto.setSku(log.getSku());
        dto.setChangeQuantity(log.getChangeQuantity());
        dto.setPreviousQuantity(log.getPreviousQuantity());
        dto.setNewQuantity(log.getNewQuantity());
        dto.setReason(log.getReason());
        dto.setPerformedBy(log.getPerformedBy());
        dto.setTimestamp(log.getTimestamp());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDto getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for Product ID: " + productId + " not found"));
        return mapToDto(inventory);
    }

    @Override
    @Transactional
    public InventoryResponseDto adjustStock(Long productId, InventoryAdjustRequestDto request, String username) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        Inventory inventory = product.getInventory();
        if (inventory == null) {
            inventory = new Inventory(product.getSku(), 0, 10, product);
        }

        int previousQty = inventory.getStockQuantity();
        int changeQty = request.getChangeQuantity();
        int newQty = previousQty + changeQty;

        if (newQty < 0) {
            throw new BadRequestException("Cannot adjust stock by " + changeQty + ". Resulting stock quantity cannot be negative (Current: " + previousQty + ").");
        }

        inventory.setStockQuantity(newQty);
        if (request.getNewThreshold() != null && request.getNewThreshold() >= 0) {
            inventory.setLowStockThreshold(request.getNewThreshold());
        }
        if (request.getWarehouseLocation() != null && !request.getWarehouseLocation().trim().isEmpty()) {
            inventory.setWarehouseLocation(request.getWarehouseLocation().trim());
        }
        if (changeQty > 0) {
            inventory.setLastRestockedAt(LocalDateTime.now());
        }
        inventory.recalculateStatus();

        Inventory saved = inventoryRepository.save(inventory);

        // Record Audit Log
        InventoryAuditLog auditLog = new InventoryAuditLog(
                product.getId(),
                product.getName(),
                product.getSku(),
                changeQty,
                previousQty,
                newQty,
                request.getReason() != null ? request.getReason() : "Manual stock adjustment",
                username != null ? username : "System"
        );
        auditLogRepository.save(auditLog);

        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryResponseDto> getLowStockAlerts() {
        return inventoryRepository.findLowOrOutOfStockInventories().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryAuditLogDto> getAuditLogs(Long productId) {
        List<InventoryAuditLog> logs = (productId != null) ?
                auditLogRepository.findByProductIdOrderByTimestampDesc(productId) :
                auditLogRepository.findTop50ByOrderByTimestampDesc();

        return logs.stream().map(this::mapToAuditDto).collect(Collectors.toList());
    }
}
