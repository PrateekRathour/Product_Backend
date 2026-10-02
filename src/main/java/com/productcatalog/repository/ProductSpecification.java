package com.productcatalog.repository;

import com.productcatalog.model.InventoryStatus;
import com.productcatalog.model.Product;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {

    public static Specification<Product> filter(
            String keyword,
            Long categoryId,
            String categorySlug,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            InventoryStatus inventoryStatus,
            String brand,
            Boolean featured,
            Boolean active) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            if (keyword != null && !keyword.trim().isEmpty()) {
                String pattern = "%" + keyword.trim().toLowerCase() + "%";
                Predicate nameLike = cb.like(cb.lower(root.get("name")), pattern);
                Predicate descLike = cb.like(cb.lower(root.get("description")), pattern);
                Predicate brandLike = cb.like(cb.lower(root.get("brand")), pattern);
                Predicate skuLike = cb.like(cb.lower(root.get("sku")), pattern);
                predicates.add(cb.or(nameLike, descLike, brandLike, skuLike));
            }

            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (categorySlug != null && !categorySlug.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("category").get("slug")), categorySlug.trim().toLowerCase()));
            }

            if (minPrice != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
            }

            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            if (brand != null && !brand.trim().isEmpty()) {
                predicates.add(cb.equal(cb.lower(root.get("brand")), brand.trim().toLowerCase()));
            }

            if (featured != null) {
                predicates.add(cb.equal(root.get("featured"), featured));
            }

            if (inventoryStatus != null) {
                predicates.add(cb.equal(root.join("inventory").get("status"), inventoryStatus));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
