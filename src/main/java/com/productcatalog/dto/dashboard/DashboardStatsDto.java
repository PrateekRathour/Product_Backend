package com.productcatalog.dto.dashboard;

import java.util.List;

public class DashboardStatsDto {

    private long totalProducts;
    private long totalCategories;
    private long totalStockUnits;
    private double totalInventoryValue;
    private long lowStockCount;
    private long outOfStockCount;
    private long activeProductsCount;
    private List<CategoryStatDto> categoryDistribution;
    private List<RecentActivityDto> recentActivities;

    public DashboardStatsDto() {
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(long totalCategories) {
        this.totalCategories = totalCategories;
    }

    public long getTotalStockUnits() {
        return totalStockUnits;
    }

    public void setTotalStockUnits(long totalStockUnits) {
        this.totalStockUnits = totalStockUnits;
    }

    public double getTotalInventoryValue() {
        return totalInventoryValue;
    }

    public void setTotalInventoryValue(double totalInventoryValue) {
        this.totalInventoryValue = totalInventoryValue;
    }

    public long getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(long lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public long getOutOfStockCount() {
        return outOfStockCount;
    }

    public void setOutOfStockCount(long outOfStockCount) {
        this.outOfStockCount = outOfStockCount;
    }

    public long getActiveProductsCount() {
        return activeProductsCount;
    }

    public void setActiveProductsCount(long activeProductsCount) {
        this.activeProductsCount = activeProductsCount;
    }

    public List<CategoryStatDto> getCategoryDistribution() {
        return categoryDistribution;
    }

    public void setCategoryDistribution(List<CategoryStatDto> categoryDistribution) {
        this.categoryDistribution = categoryDistribution;
    }

    public List<RecentActivityDto> getRecentActivities() {
        return recentActivities;
    }

    public void setRecentActivities(List<RecentActivityDto> recentActivities) {
        this.recentActivities = recentActivities;
    }
}
