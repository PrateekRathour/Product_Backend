package com.productcatalog.service;

import com.productcatalog.dto.dashboard.CategoryStatDto;
import com.productcatalog.dto.dashboard.DashboardStatsDto;
import com.productcatalog.dto.dashboard.RecentActivityDto;
import com.productcatalog.model.Category;
import com.productcatalog.model.InventoryAuditLog;
import com.productcatalog.model.Product;
import com.productcatalog.repository.CategoryRepository;
import com.productcatalog.repository.InventoryAuditLogRepository;
import com.productcatalog.repository.InventoryRepository;
import com.productcatalog.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryAuditLogRepository auditLogRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();

        stats.setTotalProducts(productRepository.count());
        stats.setTotalCategories(categoryRepository.count());
        stats.setTotalStockUnits(inventoryRepository.sumTotalStockQuantity());
        Double val = inventoryRepository.calculateTotalInventoryValue();
        stats.setTotalInventoryValue(val != null ? val : 0.0);
        stats.setLowStockCount(inventoryRepository.countLowStock());
        stats.setOutOfStockCount(inventoryRepository.countOutOfStock());

        // Category breakdown
        List<Category> categories = categoryRepository.findAll();
        List<CategoryStatDto> categoryStats = new ArrayList<>();
        for (Category cat : categories) {
            long pCount = 0;
            long units = 0;
            double valuation = 0.0;
            for (Product p : cat.getProducts()) {
                pCount++;
                if (p.getInventory() != null) {
                    units += p.getInventory().getStockQuantity();
                    valuation += p.getPrice().doubleValue() * p.getInventory().getStockQuantity();
                }
            }
            categoryStats.add(new CategoryStatDto(cat.getName(), cat.getSlug(), pCount, units, valuation));
        }
        stats.setCategoryDistribution(categoryStats);

        // Recent Audit Activities
        List<InventoryAuditLog> logs = auditLogRepository.findTop50ByOrderByTimestampDesc();
        List<RecentActivityDto> activities = new ArrayList<>();
        int limit = Math.min(logs.size(), 10);
        for (int i = 0; i < limit; i++) {
            InventoryAuditLog log = logs.get(i);
            String actionType = log.getChangeQuantity() > 0 ? "RESTOCK" : "DISPATCH";
            String title = (log.getChangeQuantity() > 0 ? "+" : "") + log.getChangeQuantity() + " units — " + log.getProductName();
            String desc = log.getReason() + " (SKU: " + log.getSku() + ", Balance: " + log.getNewQuantity() + ")";
            activities.add(new RecentActivityDto(actionType, title, desc, log.getPerformedBy(), log.getTimestamp()));
        }
        stats.setRecentActivities(activities);

        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsDto getPublicStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalProducts(productRepository.count());
        stats.setTotalCategories(categoryRepository.count());
        stats.setTotalStockUnits(inventoryRepository.sumTotalStockQuantity());
        return stats;
    }
}
