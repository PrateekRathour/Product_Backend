package com.productcatalog.repository;

import com.productcatalog.model.Inventory;
import com.productcatalog.model.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    Optional<Inventory> findBySku(String sku);

    List<Inventory> findByStatus(InventoryStatus status);

    @Query("SELECT i FROM Inventory i WHERE i.stockQuantity <= i.lowStockThreshold OR i.status = 'LOW_STOCK' OR i.status = 'OUT_OF_STOCK'")
    List<Inventory> findLowOrOutOfStockInventories();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.status = 'LOW_STOCK'")
    long countLowStock();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.status = 'OUT_OF_STOCK'")
    long countOutOfStock();

    @Query("SELECT COALESCE(SUM(i.stockQuantity), 0) FROM Inventory i")
    long sumTotalStockQuantity();

    @Query("SELECT COALESCE(SUM(p.price * i.stockQuantity), 0.0) FROM Product p JOIN p.inventory i WHERE p.active = true")
    Double calculateTotalInventoryValue();
}
