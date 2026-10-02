package com.productcatalog.dto.dashboard;

public class CategoryStatDto {

    private String categoryName;
    private String slug;
    private long productCount;
    private long totalUnits;
    private double inventoryValuation;

    public CategoryStatDto() {
    }

    public CategoryStatDto(String categoryName, String slug, long productCount, long totalUnits, double inventoryValuation) {
        this.categoryName = categoryName;
        this.slug = slug;
        this.productCount = productCount;
        this.totalUnits = totalUnits;
        this.inventoryValuation = inventoryValuation;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public long getProductCount() {
        return productCount;
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }

    public long getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(long totalUnits) {
        this.totalUnits = totalUnits;
    }

    public double getInventoryValuation() {
        return inventoryValuation;
    }

    public void setInventoryValuation(double inventoryValuation) {
        this.inventoryValuation = inventoryValuation;
    }
}
