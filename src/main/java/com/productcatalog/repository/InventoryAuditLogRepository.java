package com.productcatalog.repository;

import com.productcatalog.model.InventoryAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryAuditLogRepository extends JpaRepository<InventoryAuditLog, Long> {

    List<InventoryAuditLog> findByProductIdOrderByTimestampDesc(Long productId);

    List<InventoryAuditLog> findTop50ByOrderByTimestampDesc();
}
