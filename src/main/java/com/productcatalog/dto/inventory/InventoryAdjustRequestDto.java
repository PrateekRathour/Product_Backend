package com.productcatalog.dto.inventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class InventoryAdjustRequestDto {

    @NotNull(message = "Change quantity cannot be null")
    private Integer changeQuantity;

    @NotBlank(message = "Adjustment reason is required")
    private String reason;

    private Integer newThreshold;

    private String warehouseLocation;

    public InventoryAdjustRequestDto() {
    }

    public InventoryAdjustRequestDto(Integer changeQuantity, String reason) {
        this.changeQuantity = changeQuantity;
        this.reason = reason;
    }

    public Integer getChangeQuantity() {
        return changeQuantity;
    }

    public void setChangeQuantity(Integer changeQuantity) {
        this.changeQuantity = changeQuantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getNewThreshold() {
        return newThreshold;
    }

    public void setNewThreshold(Integer newThreshold) {
        this.newThreshold = newThreshold;
    }

    public String getWarehouseLocation() {
        return warehouseLocation;
    }

    public void setWarehouseLocation(String warehouseLocation) {
        this.warehouseLocation = warehouseLocation;
    }
}
