-- 1. Seed Men's, Kids' & Women's Showroom Categories
INSERT IGNORE INTO categories (id, segment, name, slug, hsn_code, discount_badge, description, display_order) VALUES
(1, 'MENS', 'Men''s Striped & Textured Casual Shirts', 'mens-casual-shirts', '6205', 'FREE T-SHIRT / SHORTS > ₹2000', 'EETHMAN Slim Fit Half-Sleeve Striped & Textured Linen-Cotton Shirts', 1),
(2, 'MENS', 'Men''s Premium Solid Formal & Party Shirts', 'mens-solid-shirts', '6205', 'FREE T-SHIRT / SHORTS > ₹2000', 'HOLSTEINER Power of Style 1996 Genuine Handmade Solid Shirts', 2),
(3, 'MENS', 'Men''s Yarn-Dyed Checkered Shirts', 'mens-checkered-shirts', '6205', 'FREE T-SHIRT / SHORTS > ₹2000', 'LOUIS VENTO (LV) Classic Button-Down Checkered Shirts', 3),
(4, 'MENS', 'Men''s Round Neck T-Shirts, Polos & Shorts', 'mens-tshirts-shorts', '6109', 'FREE GIFT ELIGIBLE', 'Tiruppur Combed Cotton Round Neck T-Shirts, Polo Knitwear & Casual Shorts', 4),
(5, 'KIDS', 'Kids'' Trendy Festive & Casual Wear', 'kids-wear', '6103', 'FLAT 10% - 20% OFF', 'Boys & Girls Partywear Shirts, Denim & Co-Ord Sets (10% to 20% Showroom Discount)', 5),
(6, 'WOMENS', 'Women''s Ethnic Kurtis & Trendy Tops', 'womens-wear', '6204', 'FLAT 10% - 20% OFF', 'Branded Women''s Kurtis, Co-Ord Sets & Daily Wear (10% to 20% Showroom Discount)', 6);

-- 2. Seed Real Products from Client PDF (Pages 1-35 + Showroom Segments)
INSERT IGNORE INTO products (
    id, sku_prefix, title, slug, segment, category_id, brand_name, brand_edition,
    short_description, full_description, fabric_composition, fabric_gsm, fit_type,
    sleeve_type, collar_or_neck, pattern_or_print, wash_care, weight_grams, is_featured, is_new_arrival
) VALUES
(
    1, 'KMF-EETH-STR',
    'EETHMAN Men''s Slim Fit Half-Sleeve Vertical Striped Linen-Cotton Shirt',
    'eethman-mens-slim-fit-half-sleeve-striped-shirt',
    'MENS', 1, 'EETHMAN', 'Slim Fit — Made in India',
    'Signature EETHMAN half-sleeve vertical striped casual shirt with contrast white stripes and chest pocket.',
    'Crafted for breathable all-day comfort in Tiruppur, this EETHMAN Slim Fit half-sleeve shirt features crisp vertical woven stripes on a soft linen-cotton blend with a tailored spread collar and chest pocket with woven EETHMAN tab.',
    'Linen-Cotton Textured Blend', 165, 'SLIM_FIT',
    'HALF_SLEEVE', 'Spread Collar', 'Vertical Woven Stripes',
    'Wash Separately, Gentle Machine Wash Cold, Do Not Bleach', 250, TRUE, TRUE
),
(
    2, 'KMF-EETH-PIN',
    'EETHMAN Men''s Slim Fit Half-Sleeve Textured Pin-Stripe Shirt',
    'eethman-mens-slim-fit-textured-pinstripe-shirt',
    'MENS', 1, 'EETHMAN', 'Slim Fit — Made in India',
    'Minimalist micro-textured half-sleeve casual shirt with dual-line contrast pin-stripes.',
    'Elevate your smart-casual wardrobe with the EETHMAN textured pin-stripe half-sleeve shirt. Features a breathable slub weave, tonal buttons, and signature woven pocket badge.',
    '100% Cotton Slub Weave', 170, 'SLIM_FIT',
    'HALF_SLEEVE', 'Spread Collar', 'Micro Pin-Stripe',
    'Wash Separately, Gentle Machine Wash Cold, Do Not Bleach', 250, TRUE, TRUE
),
(
    3, 'KMF-HOLS-SLD',
    'HOLSTEINER Power of Style 1996 Genuine Handmade Solid Shirt',
    'holsteiner-power-of-style-1996-handmade-solid-shirt',
    'MENS', 2, 'HOLSTEINER', 'Power of Style 1996 — Genuine Handmade Premium Edition',
    'Rich solid-colored premium shirt featuring signature tonal chest rider embroidery and contrast inner collar.',
    'From the HOLSTEINER 1996 Genuine Handmade Premium Edition collection. Tailored from high-density compacted cotton with a subtle satin sheen, tonal chest crest embroidery, and contrast geometric inner neckband trim.',
    '100% Premium Compacted Giza Cotton', 180, 'REGULAR_FIT',
    'FULL_SLEEVE', 'Classic Spread Collar', 'Solid with Tonal Chest Embroidery',
    'Gentle Wash Cold, Iron at Medium Heat, Do Not Bleach', 290, TRUE, TRUE
),
(
    4, 'KMF-LV-CHK',
    'LOUIS VENTO (LV) Classic Yarn-Dyed Checkered Button-Down Shirt',
    'louis-vento-classic-yarn-dyed-checkered-shirt',
    'MENS', 3, 'LOUIS VENTO', 'LV Classic Wear — Made in India',
    'Multi-checkered yarn-dyed cotton shirt with button-down collar and signature LV crest embroidery.',
    'Authentic LOUIS VENTO (LV) yarn-dyed tartan and windowpane checkered shirt. Features a structured button-down collar, chest pocket with embroidered crest, and colorfast yarn-dyed cotton construction.',
    '100% Yarn-Dyed Combed Cotton', 175, 'REGULAR_FIT',
    'FULL_SLEEVE', 'Button-Down Collar', 'Yarn-Dyed Tartan Checks',
    'Wash Dark Colors Separately, Do Not Bleach', 285, TRUE, TRUE
),
(
    5, 'KMF-TSH-CMB',
    'Kiruthis Signature Tiruppur 180 GSM Bio-Washed Combed Cotton T-Shirt & Lounge Combo',
    'kiruthis-signature-tiruppur-bio-washed-tshirt',
    'MENS', 4, 'KIRUTHIS SIGNATURE', 'Tiruppur Export Knitwear Edition',
    'Super-soft 180 GSM bio-washed 100% combed cotton round-neck T-shirt crafted in Tiruppur.',
    'Direct from Tiruppur knitwear mills to our Amman Nagar showroom. Enzyme bio-washed for zero pilling, lycra rib crew neck, and breathable all-season comfort.',
    '100% Super Combed Bio-Washed Cotton', 180, 'REGULAR_FIT',
    'HALF_SLEEVE', 'Lycra Rib Round Neck', 'Solid Bio-Wash Knit',
    'Machine Wash Cold, Do Not Iron on Print', 220, TRUE, TRUE
),
(
    6, 'KMF-KIDS-FST',
    'Kiruthis Junior Boys'' Festive Cotton Printed Shirt & Cotton Chino Set (10%–20% OFF)',
    'kiruthis-junior-boys-festive-shirt-chino-set',
    'KIDS', 5, 'KIRUTHIS JUNIOR', 'Kids'' Trendy Showroom Collection — Flat 20% Off',
    'Soft breathable cotton partywear shirt paired with stretchable cotton twill chinos for boys.',
    'Specially curated for festivals and family occasions at Kiruthis Mens Fashions. Skin-friendly 100% cotton weave with adjustable inner-waist elastic trousers. Eligible for our Flat 10% to 20% Kids Showroom Discount.',
    '100% Soft Combed Cotton & Stretch Twill', 160, 'KIDS_COMFORT_FIT',
    'HALF_SLEEVE', 'Cuban Camp Collar', 'Festive Micro-Motif Print',
    'Gentle Hand Wash Cold', 260, TRUE, TRUE
),
(
    7, 'KMF-WMN-KRT',
    'Kiruthis Trendy Women''s Festive Foil-Printed Rayon Straight Kurti & Pant Set (20% OFF)',
    'kiruthis-womens-festive-rayon-kurti-pant-set',
    'WOMENS', 6, 'KIRUTHIS TRENDY', 'Women''s Branded Showroom Collection — Flat 20% Off',
    'Elegant zari-highlighted straight kurti with matching ankle-length pants in breathable Liva-grade rayon.',
    'Part of the Kiruthis Women''s Showroom Collection with Flat 10% to 20% Discount. Features delicate neckline embroidery, 3/4 sleeves, and super-soft drape.',
    'Premium Liva-Grade Slub Rayon', 170, 'REGULAR_FIT',
    'FULL_SLEEVE', 'Embroidered Notch Neck', 'Festive Ethnic Foil Print',
    'Gentle Hand Wash Separately', 310, TRUE, TRUE
);

-- 3. Seed Real Color & Size Variants
INSERT IGNORE INTO product_variants (
    id, product_id, sku_code, color_name, color_hex, size_label, chest_or_waist_inches,
    mrp, selling_price, gst_percentage, stock_quantity
) VALUES
-- Product 1: EETHMAN Striped Shirts (Pages 1-3)
(1, 1, 'KMF-EETH-STR-SKY-M', 'Sky Blue Stripe', '#7DD3FC', 'M', 'Chest: 40 in | Length: 28 in', 1199.00, 699.00, 5.00, 15),
(2, 1, 'KMF-EETH-STR-SKY-L', 'Sky Blue Stripe', '#7DD3FC', 'L', 'Chest: 42 in | Length: 29 in', 1199.00, 699.00, 5.00, 20),
(3, 1, 'KMF-EETH-STR-MNT-L', 'Mint Green Stripe', '#86EFAC', 'L', 'Chest: 42 in | Length: 29 in', 1199.00, 699.00, 5.00, 14),
(4, 1, 'KMF-EETH-STR-FOR-XL', 'Forest Green Stripe', '#14532D', 'XL', 'Chest: 44 in | Length: 30 in', 1199.00, 699.00, 5.00, 12),
(5, 1, 'KMF-EETH-STR-PNK-M', 'Pastel Pink Stripe', '#F9A8D4', 'M', 'Chest: 40 in | Length: 28 in', 1199.00, 699.00, 5.00, 10),
(6, 1, 'KMF-EETH-STR-BRN-L', 'Taupe Brown Stripe', '#A8A29E', 'L', 'Chest: 42 in | Length: 29 in', 1199.00, 699.00, 5.00, 16),

-- Product 2: EETHMAN Textured Pin-Stripe Shirts (Pages 4-5)
(7, 2, 'KMF-EETH-PIN-GRY-L', 'Slate Grey Pin-Stripe', '#64748B', 'L', 'Chest: 42 in | Length: 29 in', 1249.00, 749.00, 5.00, 18),
(8, 2, 'KMF-EETH-PIN-CRM-M', 'Cream White Pin-Stripe', '#F5F5F4', 'M', 'Chest: 40 in | Length: 28 in', 1249.00, 749.00, 5.00, 15),
(9, 2, 'KMF-EETH-PIN-NVY-XL', 'Deep Navy Pin-Stripe', '#1E3A8A', 'XL', 'Chest: 44 in | Length: 30 in', 1249.00, 749.00, 5.00, 12),

-- Product 3: HOLSTEINER 1996 Solid Shirts (Pages 6-14)
(10, 3, 'KMF-HOLS-SLD-BLK-M', 'Jet Black', '#0F172A', 'M', 'Chest: 40 in | Length: 28.5 in', 1499.00, 899.00, 5.00, 22),
(11, 3, 'KMF-HOLS-SLD-BLK-L', 'Jet Black', '#0F172A', 'L', 'Chest: 42 in | Length: 29.5 in', 1499.00, 899.00, 5.00, 25),
(12, 3, 'KMF-HOLS-SLD-MRN-L', 'Wine Maroon', '#881337', 'L', 'Chest: 42 in | Length: 29.5 in', 1499.00, 899.00, 5.00, 18),
(13, 3, 'KMF-HOLS-SLD-WHT-L', 'Crisp White', '#F8FAFC', 'L', 'Chest: 42 in | Length: 29.5 in', 1499.00, 899.00, 5.00, 20),
(14, 3, 'KMF-HOLS-SLD-EMR-XL', 'Emerald Green', '#065F46', 'XL', 'Chest: 44 in | Length: 30 in', 1499.00, 899.00, 5.00, 14),
(15, 3, 'KMF-HOLS-SLD-RSE-M', 'Dusty Rose Pink', '#FDA4AF', 'M', 'Chest: 40 in | Length: 28.5 in', 1499.00, 899.00, 5.00, 12),
(16, 3, 'KMF-HOLS-SLD-BGE-L', 'Champagne Beige', '#E7E5E4', 'L', 'Chest: 42 in | Length: 29.5 in', 1499.00, 899.00, 5.00, 16),

-- Product 4: LOUIS VENTO Checkered Shirts (Pages 15-35)
(17, 4, 'KMF-LV-CHK-BLK-M', 'Black & White Tartan Check', '#18181B', 'M', 'Chest: 40 in | Length: 28.5 in', 1399.00, 849.00, 5.00, 16),
(18, 4, 'KMF-LV-CHK-BLK-L', 'Black & White Tartan Check', '#18181B', 'L', 'Chest: 42 in | Length: 29.5 in', 1399.00, 849.00, 5.00, 20),
(19, 4, 'KMF-LV-CHK-SKY-L', 'Sky Blue & White Check', '#38BDF8', 'L', 'Chest: 42 in | Length: 29.5 in', 1399.00, 849.00, 5.00, 15),
(20, 4, 'KMF-LV-CHK-COF-XL', 'Coffee Brown Check', '#78350F', 'XL', 'Chest: 44 in | Length: 30 in', 1399.00, 849.00, 5.00, 14),
(21, 4, 'KMF-LV-CHK-RYL-L', 'Navy & Royal Blue Check', '#1D4ED8', 'L', 'Chest: 42 in | Length: 29.5 in', 1399.00, 849.00, 5.00, 18),

-- Product 5: Kiruthis Signature Tiruppur Round Neck T-Shirt
(22, 5, 'KMF-TSH-CMB-NVY-M', 'Royal Navy Blue', '#1E3A8A', 'M', 'Chest: 39 in | Length: 27.5 in', 699.00, 449.00, 5.00, 30),
(23, 5, 'KMF-TSH-CMB-NVY-L', 'Royal Navy Blue', '#1E3A8A', 'L', 'Chest: 41 in | Length: 28.5 in', 699.00, 449.00, 5.00, 28),
(24, 5, 'KMF-TSH-CMB-OLV-XL', 'Olive Green', '#3F6212', 'XL', 'Chest: 43 in | Length: 29.5 in', 699.00, 449.00, 5.00, 22),

-- Product 6: Kids' Festive Shirt & Chino Set (Flat 20% Showroom Discount)
(25, 6, 'KMF-KIDS-FST-YLW-5Y', 'Mustard Gold & Navy', '#EAB308', '4-5 Yrs', 'Chest: 26 in | Waist: 22 in', 1199.00, 959.00, 5.00, 14),
(26, 6, 'KMF-KIDS-FST-TEAL-7Y', 'Teal Blue & Beige', '#0D9488', '6-7 Yrs', 'Chest: 28 in | Waist: 24 in', 1199.00, 959.00, 5.00, 16),
(27, 6, 'KMF-KIDS-FST-MRN-9Y', 'Festive Maroon & Cream', '#9F1239', '8-9 Yrs', 'Chest: 30 in | Waist: 25 in', 1199.00, 959.00, 5.00, 12),

-- Product 7: Women's Festive Kurti & Pant Set (Flat 20% Showroom Discount)
(28, 7, 'KMF-WMN-KRT-RNI-M', 'Rani Pink Zari', '#BE185D', 'M', 'Bust: 38 in | Length: 44 in', 1499.00, 1199.00, 12.00, 15),
(29, 7, 'KMF-WMN-KRT-PCK-L', 'Peacock Teal Zari', '#115E59', 'L', 'Bust: 40 in | Length: 44 in', 1499.00, 1199.00, 12.00, 18),
(30, 7, 'KMF-WMN-KRT-wine-XL', 'Royal Wine Zari', '#581C87', 'XL', 'Bust: 42 in | Length: 45 in', 1499.00, 1199.00, 12.00, 12);

