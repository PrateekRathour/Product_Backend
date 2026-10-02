package com.productcatalog.config;

import com.productcatalog.model.*;
import com.productcatalog.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryAuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        logger.info("Initializing Product Catalog master data...");

        // 1. Initialize Roles
        Role roleAdmin = roleRepository.findByName(ERole.ROLE_ADMIN).orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_ADMIN)));
        Role roleManager = roleRepository.findByName(ERole.ROLE_MANAGER).orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_MANAGER)));
        Role roleUser = roleRepository.findByName(ERole.ROLE_USER).orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_USER)));

        // 2. Initialize Default Users
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@productcatalog.com", passwordEncoder.encode("Admin@1234"), "Master Administrator");
            admin.setAvatarUrl("https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80");
            admin.setRoles(new HashSet<>(Arrays.asList(roleAdmin, roleManager, roleUser)));
            userRepository.save(admin);
            logger.info("Default Admin created: username=admin, password=Admin@1234");
        }

        if (!userRepository.existsByUsername("manager")) {
            User manager = new User("manager", "manager@productcatalog.com", passwordEncoder.encode("Manager@1234"), "Inventory Manager");
            manager.setAvatarUrl("https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80");
            manager.setRoles(new HashSet<>(Arrays.asList(roleManager, roleUser)));
            userRepository.save(manager);
            logger.info("Default Manager created: username=manager, password=Manager@1234");
        }

        if (!userRepository.existsByUsername("user")) {
            User user = new User("user", "user@productcatalog.com", passwordEncoder.encode("User@1234"), "Demo Customer");
            user.setAvatarUrl("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80");
            user.setRoles(new HashSet<>(Collections.singletonList(roleUser)));
            userRepository.save(user);
            logger.info("Default User created: username=user, password=User@1234");
        }

        // 3. Initialize Categories and Products if empty
        if (categoryRepository.count() == 0) {
            logger.info("Seeding categories and realistic product catalog with inventory...");

            Category catLaptops = categoryRepository.save(new Category(
                    "Electronics & Laptops", "electronics-laptops",
                    "High performance computing, flagship laptops, and cutting-edge silicon.",
                    "laptop", "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80", 1));

            Category catSmartphones = categoryRepository.save(new Category(
                    "Smartphones & Tablets", "smartphones-tablets",
                    "Next-generation smartphones, foldable displays, and stylus tablets.",
                    "smartphone", "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&auto=format&fit=crop&q=80", 2));

            Category catAudio = categoryRepository.save(new Category(
                    "Audio & Acoustics", "audio-acoustics",
                    "Audiophile headphones, noise-cancelling earbuds, and wireless spatial audio.",
                    "headphones", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80", 3));

            Category catWearables = categoryRepository.save(new Category(
                    "Wearables & Smartwatches", "wearables-smartwatches",
                    "Health tracking smartwatches, bio-sensors, and active lifestyle bands.",
                    "watch", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&auto=format&fit=crop&q=80", 4));

            Category catOptics = categoryRepository.save(new Category(
                    "Photography & Drones", "photography-drones",
                    "Professional mirrorless cameras, 4K camera drones, and cinema optics.",
                    "camera", "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&auto=format&fit=crop&q=80", 5));

            Category catGaming = categoryRepository.save(new Category(
                    "Gaming & Peripherals", "gaming-peripherals",
                    "Mechanical keyboards, ultra-light precision mice, and esports monitors.",
                    "gamepad", "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=800&auto=format&fit=crop&q=80", 6));

            // Seed Products
            createSeedProduct("MacBook Pro 16\" M3 Max", "macbook-pro-16-m3-max", "SKU-MBP-16-M3",
                    "Apple M3 Max 16-core CPU, 40-core GPU, 64GB Unified Memory, 1TB Liquid Retina XDR.",
                    new BigDecimal("349900.00"), new BigDecimal("329900.00"),
                    "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=800&auto=format&fit=crop&q=80",
                    "Apple", true, new BigDecimal("4.9"), catLaptops, 42, 10, "Silicon Bay (WH-A1)");

            createSeedProduct("Dell XPS 15 OLED InfinityEdge", "dell-xps-15-oled", "SKU-DELL-XPS15",
                    "13th Gen Intel Core i9, 32GB DDR5, RTX 4070 8GB, 3.5K OLED Touch Display with 100% DCI-P3.",
                    new BigDecimal("229900.00"), new BigDecimal("209900.00"),
                    "https://images.unsplash.com/photo-1593642632823-8f785ba67e45?w=800&auto=format&fit=crop&q=80",
                    "Dell", true, new BigDecimal("4.8"), catLaptops, 18, 8, "Silicon Bay (WH-A2)");

            createSeedProduct("ThinkPad X1 Carbon Gen 11", "thinkpad-x1-carbon-gen-11", "SKU-LEN-X1C11",
                    "Ultralight carbon-fiber chassis, Intel Core i7-1365U, 32GB RAM, 1TB SSD, 14-inch 2.8K OLED display.",
                    new BigDecimal("184900.00"), null,
                    "https://images.unsplash.com/photo-1588872657578-7efd1f1555ed?w=800&auto=format&fit=crop&q=80",
                    "Lenovo", false, new BigDecimal("4.7"), catLaptops, 4, 8, "Silicon Bay (WH-A3)");

            createSeedProduct("iPhone 16 Pro Max Titanium", "iphone-16-pro-max-titanium", "SKU-IPH-16PM",
                    "A18 Pro chip with 6-core GPU, Grade 5 Titanium design, 48MP Fusion camera with 5x Telephoto.",
                    new BigDecimal("144900.00"), new BigDecimal("134900.00"),
                    "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=800&auto=format&fit=crop&q=80",
                    "Apple", true, new BigDecimal("4.9"), catSmartphones, 65, 15, "Mobile Hub (WH-B1)");

            createSeedProduct("Samsung Galaxy S24 Ultra AI", "samsung-galaxy-s24-ultra", "SKU-SAM-S24U",
                    "Snapdragon 8 Gen 3, Galaxy AI suite, 200MP quad camera system, built-in S-Pen, Titanium Gray.",
                    new BigDecimal("129999.00"), new BigDecimal("119999.00"),
                    "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=800&auto=format&fit=crop&q=80",
                    "Samsung", true, new BigDecimal("4.8"), catSmartphones, 29, 10, "Mobile Hub (WH-B2)");

            createSeedProduct("Google Pixel 9 Pro Fold", "google-pixel-9-pro-fold", "SKU-GOOG-P9F",
                    "Google Tensor G4, 8.0-inch Super Actua Flex inner display, Gemini Advanced AI integration.",
                    new BigDecimal("172999.00"), null,
                    "https://images.unsplash.com/photo-1598327105666-5b89351aff97?w=800&auto=format&fit=crop&q=80",
                    "Google", false, new BigDecimal("4.6"), catSmartphones, 3, 5, "Mobile Hub (WH-B3)");

            createSeedProduct("Sony WH-1000XM5 Noise Canceling", "sony-wh-1000xm5-headphones", "SKU-SNY-XM5",
                    "Industry-leading noise cancellation with 8 microphones, LDAC Hi-Res Audio, 30-hour battery life.",
                    new BigDecimal("29990.00"), new BigDecimal("26990.00"),
                    "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=800&auto=format&fit=crop&q=80",
                    "Sony", true, new BigDecimal("4.9"), catAudio, 54, 12, "Acoustics Center (WH-C1)");

            createSeedProduct("Bose QuietComfort Ultra", "bose-quietcomfort-ultra", "SKU-BOSE-QCU",
                    "Breakthrough spatialized audio for more immersive listening, CustomTune technology, quiet comfort.",
                    new BigDecimal("35900.00"), new BigDecimal("31900.00"),
                    "https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=800&auto=format&fit=crop&q=80",
                    "Bose", false, new BigDecimal("4.7"), catAudio, 22, 8, "Acoustics Center (WH-C2)");

            createSeedProduct("Apple AirPods Max Space Gray", "apple-airpods-max-space-gray", "SKU-APP-APMAX",
                    "Apple H1 chip in each cup, Active Noise Cancellation with Transparency mode, knit-mesh canopy.",
                    new BigDecimal("59900.00"), new BigDecimal("54900.00"),
                    "https://images.unsplash.com/photo-1583394838336-acd977736f90?w=800&auto=format&fit=crop&q=80",
                    "Apple", false, new BigDecimal("4.8"), catAudio, 0, 6, "Acoustics Center (WH-C3)");

            createSeedProduct("Apple Watch Ultra 2 Titanium", "apple-watch-ultra-2", "SKU-APP-AWU2",
                    "49mm aerospace-grade titanium case, 3000-nit Always-On display, dual-frequency precision GPS.",
                    new BigDecimal("89900.00"), new BigDecimal("84900.00"),
                    "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=800&auto=format&fit=crop&q=80",
                    "Apple", true, new BigDecimal("4.9"), catWearables, 31, 10, "Wearables Dept (WH-D1)");

            createSeedProduct("Garmin Fenix 7X Pro Sapphire Solar", "garmin-fenix-7x-pro", "SKU-GAR-F7XP",
                    "Solar-charging multisport GPS watch with built-in LED flashlight, endurance score, topographical maps.",
                    new BigDecimal("98990.00"), new BigDecimal("92990.00"),
                    "https://images.unsplash.com/photo-1508685096489-7aacd43bd3b1?w=800&auto=format&fit=crop&q=80",
                    "Garmin", false, new BigDecimal("4.8"), catWearables, 5, 8, "Wearables Dept (WH-D2)");

            createSeedProduct("Sony Alpha a7 IV Full-Frame Camera", "sony-alpha-a7-iv", "SKU-SNY-A7M4",
                    "33MP full-frame Exmor R sensor, BIONZ XR processing engine, 4K 60p 10-bit recording, Real-time Eye AF.",
                    new BigDecimal("242490.00"), new BigDecimal("224990.00"),
                    "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=800&auto=format&fit=crop&q=80",
                    "Sony", true, new BigDecimal("4.9"), catOptics, 14, 5, "Optics Vault (WH-E1)");

            createSeedProduct("DJI Mavic 3 Pro Cine Drone", "dji-mavic-3-pro-cine", "SKU-DJI-M3PC",
                    "Triple camera optical system with Hasselblad 4/3 CMOS, Apple ProRes 422 HQ support, 43 min flight.",
                    new BigDecimal("389900.00"), new BigDecimal("369900.00"),
                    "https://images.unsplash.com/photo-1508614589041-895b88991e3e?w=800&auto=format&fit=crop&q=80",
                    "DJI", true, new BigDecimal("5.0"), catOptics, 2, 4, "Optics Vault (WH-E2)");

            createSeedProduct("Logitech G PRO X Superlight 2", "logitech-g-pro-x-superlight-2", "SKU-LOG-GPX2",
                    "HERO 2 sensor with 32,000 DPI, LIGHTFORCE hybrid switches, 60g ultralight wireless design.",
                    new BigDecimal("14995.00"), new BigDecimal("12995.00"),
                    "https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=800&auto=format&fit=crop&q=80",
                    "Logitech", true, new BigDecimal("4.8"), catGaming, 78, 20, "Gaming Lounge (WH-F1)");

            createSeedProduct("Keychron Q1 Pro Wireless Custom Keyboard", "keychron-q1-pro-keyboard", "SKU-KEY-Q1P",
                    "Full CNC aluminum body, 75% layout, hot-swappable double-gasket design, QMK/VIA programmable.",
                    new BigDecimal("18990.00"), null,
                    "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=800&auto=format&fit=crop&q=80",
                    "Keychron", false, new BigDecimal("4.9"), catGaming, 25, 10, "Gaming Lounge (WH-F2)");

            logger.info("Master catalog initialization completed successfully.");
        }
    }

    private void createSeedProduct(String name, String slug, String sku, String description,
                                   BigDecimal price, BigDecimal discountPrice, String imageUrl,
                                   String brand, boolean featured, BigDecimal rating,
                                   Category category, int stockQuantity, int lowStockThreshold,
                                   String warehouseLocation) {
        Product p = new Product();
        p.setName(name);
        p.setSlug(slug);
        p.setSku(sku);
        p.setDescription(description);
        p.setPrice(price);
        p.setDiscountPrice(discountPrice);
        p.setImageUrl(imageUrl);
        p.setBrand(brand);
        p.setFeatured(featured);
        p.setActive(true);
        p.setRating(rating);
        p.setCategory(category);

        Product savedProduct = productRepository.save(p);

        Inventory inv = new Inventory();
        inv.setSku(sku);
        inv.setStockQuantity(stockQuantity);
        inv.setReservedQuantity(0);
        inv.setLowStockThreshold(lowStockThreshold);
        inv.setWarehouseLocation(warehouseLocation);
        inv.setProduct(savedProduct);
        inv.recalculateStatus();

        Inventory savedInventory = inventoryRepository.save(inv);
        savedProduct.setInventory(savedInventory);

        if (stockQuantity > 0) {
            InventoryAuditLog log = new InventoryAuditLog(
                    savedProduct.getId(),
                    savedProduct.getName(),
                    savedProduct.getSku(),
                    stockQuantity,
                    0,
                    stockQuantity,
                    "Initial stock shipment intake",
                    "System Admin"
            );
            auditLogRepository.save(log);
        }
    }
}
