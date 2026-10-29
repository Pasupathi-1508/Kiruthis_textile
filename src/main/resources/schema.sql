-- ============================================================================
-- KIRUTHIS MENS FASHIONS (AMMAN NAGAR BUS STOP, GANAPATHIPALAYAM RD, TIRUPUR)
-- PRODUCTION MYSQL 8.0 DATABASE SCHEMA
-- ============================================================================

CREATE DATABASE IF NOT EXISTS kiruthis_fashions_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE kiruthis_fashions_db;

-- ----------------------------------------------------------------------------
-- 1. USERS TABLE (Customers & Showroom Admin/Staff)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    email VARCHAR(150) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('ROLE_CUSTOMER', 'ROLE_ADMIN', 'ROLE_STAFF') NOT NULL DEFAULT 'ROLE_CUSTOMER',
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_phone (phone),
    INDEX idx_users_role (role)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 2. CUSTOMER ADDRESSES TABLE (Pan-India Pin Code & State for GST Calculation)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS customer_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    recipient_name VARCHAR(120) NOT NULL,
    phone VARCHAR(15) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    landmark VARCHAR(150),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    state_code VARCHAR(5) NOT NULL DEFAULT '33', -- '33' = Tamil Nadu (CGST+SGST), others = IGST
    pincode VARCHAR(6) NOT NULL,
    address_type ENUM('HOME', 'WORK', 'OTHER') NOT NULL DEFAULT 'HOME',
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_address_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_address_user (user_id),
    INDEX idx_address_pincode (pincode)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 3. CATEGORIES TABLE (MENS | KIDS | WOMENS Segments & Apparel HSN Codes)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    segment ENUM('MENS', 'KIDS', 'WOMENS') NOT NULL,
    name VARCHAR(100) NOT NULL,
    slug VARCHAR(120) NOT NULL UNIQUE,
    parent_id BIGINT NULL,
    hsn_code VARCHAR(12) NOT NULL, -- e.g. 6205 (Men's Shirts), 6109 (T-Shirts), 6203 (Jeans/Trousers)
    discount_badge VARCHAR(60),    -- e.g. '10% - 20% OFF' for Kids & Women's categories
    description VARCHAR(500),
    image_url VARCHAR(500),
    display_order INT NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_category_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL,
    INDEX idx_category_segment (segment, is_active),
    INDEX idx_category_slug (slug)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 4. PRODUCTS TABLE (Master Garment Details for EETHMAN, HOLSTEINER, LOUIS VENTO)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku_prefix VARCHAR(40) NOT NULL UNIQUE,
    title VARCHAR(200) NOT NULL,
    slug VARCHAR(220) NOT NULL UNIQUE,
    segment ENUM('MENS', 'KIDS', 'WOMENS') NOT NULL,
    category_id BIGINT NOT NULL,
    brand_name VARCHAR(100) NOT NULL DEFAULT 'Kiruthis Signature', -- 'EETHMAN', 'HOLSTEINER', 'LOUIS VENTO'
    brand_edition VARCHAR(120),               -- e.g. 'Power of Style 1996 - Genuine Handmade Premium Edition'
    short_description VARCHAR(500),
    full_description TEXT,
    
    -- Tiruppur Garment & Textile Specifications
    fabric_composition VARCHAR(120) NOT NULL, -- e.g. 'Pure Linen-Cotton Blend', '100% Combed Compact Cotton'
    fabric_gsm INT NULL,
    fit_type ENUM('REGULAR_FIT', 'SLIM_FIT', 'OVERSIZED_FIT', 'RELAXED_FIT', 'KIDS_COMFORT_FIT') NOT NULL DEFAULT 'REGULAR_FIT',
    sleeve_type ENUM('HALF_SLEEVE', 'FULL_SLEEVE', 'SLEEVELESS', 'ROLL_UP_SLEEVE', 'NOT_APPLICABLE') NOT NULL DEFAULT 'HALF_SLEEVE',
    collar_or_neck VARCHAR(80),               -- e.g. 'Button-Down Collar', 'Spread Collar', 'Round Neck'
    pattern_or_print VARCHAR(80),             -- e.g. 'Vertical Stripes', 'Solid Tonal Crest', 'Yarn-Dyed Checks'
    wash_care VARCHAR(200) DEFAULT 'Wash Separately, Gentle Machine Wash Cold, Do Not Bleach',
    weight_grams INT NOT NULL DEFAULT 280,
    
    -- Showroom Flags & Ratings
    is_featured BOOLEAN NOT NULL DEFAULT FALSE,
    is_new_arrival BOOLEAN NOT NULL DEFAULT TRUE,
    is_cod_eligible BOOLEAN NOT NULL DEFAULT TRUE,
    is_returnable BOOLEAN NOT NULL DEFAULT TRUE,
    average_rating DECIMAL(3,2) NOT NULL DEFAULT 4.70,
    review_count INT NOT NULL DEFAULT 18,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_product_segment_cat (segment, category_id, is_active),
    INDEX idx_product_brand (brand_name, is_active),
    INDEX idx_product_slug (slug),
    FULLTEXT INDEX ft_product_search (title, brand_name, fabric_composition, pattern_or_print)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 5. PRODUCT VARIANTS TABLE (Size x Color SKU, Pricing, GST & Live Stock)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product_variants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    sku_code VARCHAR(60) NOT NULL UNIQUE,
    color_name VARCHAR(60) NOT NULL,
    color_hex VARCHAR(10) NOT NULL DEFAULT '#1E293B',
    size_label VARCHAR(30) NOT NULL,          -- 'S', 'M', 'L', 'XL', 'XXL', '4-5Y', '6-7Y', etc.
    chest_or_waist_inches VARCHAR(40),
    
    -- Pricing & GST (5% up to Rs. 1000, 12% above Rs. 1000)
    mrp DECIMAL(10,2) NOT NULL,
    selling_price DECIMAL(10,2) NOT NULL,
    gst_percentage DECIMAL(5,2) NOT NULL DEFAULT 5.00,
    
    -- Transactional Stock Control
    stock_quantity INT NOT NULL DEFAULT 0,
    reserved_quantity INT NOT NULL DEFAULT 0,
    low_stock_threshold INT NOT NULL DEFAULT 5,
    version BIGINT NOT NULL DEFAULT 0,        -- JPA @Version optimistic locking to prevent overselling
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_variant_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT chk_stock_non_negative CHECK (stock_quantity >= 0 AND reserved_quantity >= 0),
    INDEX idx_variant_product (product_id, is_active),
    INDEX idx_variant_size_color (size_label, color_name),
    INDEX idx_variant_price (selling_price)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 6. PRODUCT IMAGES TABLE (Multi-Angle Gallery & Fabric Close-Up Zoom)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS product_images (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    variant_id BIGINT NULL,
    image_url VARCHAR(500) NOT NULL,
    image_type ENUM('PRIMARY_FRONT', 'BACK_VIEW', 'SIDE_FIT', 'FABRIC_ZOOM', 'SIZE_CHART') NOT NULL DEFAULT 'PRIMARY_FRONT',
    display_order INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_image_product FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    CONSTRAINT fk_image_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE SET NULL,
    INDEX idx_image_product (product_id, display_order)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 7. SHOPPING CART ITEMS TABLE
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS cart_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    session_token VARCHAR(100) NULL,
    variant_id BIGINT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id) ON DELETE CASCADE,
    INDEX idx_cart_user (user_id),
    INDEX idx_cart_session (session_token)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 8. ORDERS TABLE (GST Invoice Breakdown, ₹2000 Free Gift & Courier AWB)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(40) NOT NULL UNIQUE, -- e.g. 'KMF-2026-10001'
    invoice_number VARCHAR(40) UNIQUE,        -- e.g. 'INV-KMF-2026-0001'
    user_id BIGINT NULL,
    
    -- Customer & Shipping Snapshot
    customer_name VARCHAR(120) NOT NULL,
    customer_phone VARCHAR(15) NOT NULL,
    customer_email VARCHAR(150),
    shipping_address_line1 VARCHAR(255) NOT NULL,
    shipping_address_line2 VARCHAR(255),
    shipping_landmark VARCHAR(150),
    shipping_city VARCHAR(100) NOT NULL,
    shipping_state VARCHAR(100) NOT NULL,
    shipping_state_code VARCHAR(5) NOT NULL DEFAULT '33',
    shipping_pincode VARCHAR(6) NOT NULL,
    
    -- Financial & GST Breakdown (Showroom GSTIN: 33JODPK0345L1ZW)
    total_taxable_amount DECIMAL(10,2) NOT NULL,
    cgst_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    sgst_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    igst_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    shipping_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    cod_handling_fee DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    grand_total DECIMAL(10,2) NOT NULL,

    -- Showroom Promotional Offer (Orders >= Rs. 2,000 Free T-Shirt / Shorts + Assured Gift)
    free_gift_eligible BOOLEAN NOT NULL DEFAULT FALSE,
    free_gift_choice ENUM('NONE', 'FREE_ROUND_NECK_TSHIRT', 'FREE_BRANDED_SHORTS', 'ASSURED_SURPRISE_GIFT') NOT NULL DEFAULT 'NONE',
    free_gift_size VARCHAR(20) NULL,          -- e.g. 'M', 'L', 'XL' selected by customer at checkout
    
    -- Payment Gateway & COD Verification
    payment_mode ENUM('ONLINE_PREPAID', 'COD') NOT NULL,
    payment_status ENUM('PENDING', 'PAID', 'COD_OTP_VERIFIED', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    gateway_order_id VARCHAR(100),
    gateway_payment_id VARCHAR(100),
    gateway_signature VARCHAR(255),
    
    -- Order Fulfillment & Courier Aggregator
    order_status ENUM(
        'CREATED',
        'PAYMENT_PENDING',
        'CONFIRMED',
        'PACKED',
        'SHIPPED',
        'OUT_FOR_DELIVERY',
        'DELIVERED',
        'CANCELLED',
        'RETURN_REQUESTED',
        'RETURN_IN_TRANSIT',
        'RETURNED_AND_REFUNDED',
        'RTO_UNDELIVERED'
    ) NOT NULL DEFAULT 'CREATED',
    total_weight_grams INT NOT NULL DEFAULT 350,
    courier_name VARCHAR(80),
    awb_tracking_number VARCHAR(80),
    shipping_label_url VARCHAR(500),
    delivered_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_order_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_order_number (order_number),
    INDEX idx_order_status (order_status, created_at),
    INDEX idx_order_phone (customer_phone)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 9. ORDER ITEMS TABLE (Line-Item HSN, Size, Price & Tax Snapshot)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    variant_id BIGINT NOT NULL,
    sku_code VARCHAR(60) NOT NULL,
    product_title VARCHAR(200) NOT NULL,
    color_name VARCHAR(60) NOT NULL,
    size_label VARCHAR(30) NOT NULL,
    hsn_code VARCHAR(12) NOT NULL,
    quantity INT NOT NULL,
    unit_selling_price DECIMAL(10,2) NOT NULL,
    gst_percentage DECIMAL(5,2) NOT NULL,
    taxable_value DECIMAL(10,2) NOT NULL,
    cgst_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    sgst_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    igst_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    line_total DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_order_item_variant FOREIGN KEY (variant_id) REFERENCES product_variants(id),
    INDEX idx_order_item_order (order_id)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 10. ORDER TRACKING EVENTS TABLE (Live Courier Webhook Checkpoints)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS order_tracking_events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    status_code VARCHAR(50) NOT NULL,
    location_hub VARCHAR(150) NOT NULL,
    remarks VARCHAR(500) NOT NULL,
    event_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tracking_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    INDEX idx_tracking_order (order_id, event_timestamp)
) ENGINE=InnoDB;

-- ----------------------------------------------------------------------------
-- 11. RETURN & EXCHANGE REQUESTS TABLE (7-Day Doorstep Reverse Pickup)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS return_requests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    order_item_id BIGINT NOT NULL,
    request_type ENUM('SIZE_EXCHANGE', 'REFUND') NOT NULL DEFAULT 'SIZE_EXCHANGE',
    exchange_new_variant_id BIGINT NULL,
    reason VARCHAR(255) NOT NULL,
    customer_comments VARCHAR(500),
    unboxing_photo_url VARCHAR(500),
    refund_upi_id VARCHAR(100),
    return_status ENUM('PENDING_APPROVAL', 'APPROVED_RVP_SCHEDULED', 'PICKED_UP', 'QC_PASSED', 'QC_REJECTED', 'COMPLETED') NOT NULL DEFAULT 'PENDING_APPROVAL',
    reverse_awb_number VARCHAR(80),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_return_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
    CONSTRAINT fk_return_item FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE
) ENGINE=InnoDB;