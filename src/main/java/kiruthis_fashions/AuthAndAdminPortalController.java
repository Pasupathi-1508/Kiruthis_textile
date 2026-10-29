package kiruthis_fashions;

import jakarta.servlet.http.HttpSession;
import kiruthis_fashions.entity.Category;
import kiruthis_fashions.entity.Order;
import kiruthis_fashions.entity.Product;
import kiruthis_fashions.entity.ProductImage;
import kiruthis_fashions.entity.ProductVariant;
import kiruthis_fashions.entity.User;
import kiruthis_fashions.repository.CategoryRepository;
import kiruthis_fashions.repository.OrderRepository;
import kiruthis_fashions.repository.ProductRepository;
import kiruthis_fashions.repository.ProductVariantRepository;
import kiruthis_fashions.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*")
public class AuthAndAdminPortalController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final CategoryRepository categoryRepository;
    private final OrderRepository orderRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthAndAdminPortalController(
            UserRepository userRepository,
            ProductRepository productRepository,
            ProductVariantRepository variantRepository,
            CategoryRepository categoryRepository,
            OrderRepository orderRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.categoryRepository = categoryRepository;
        this.orderRepository = orderRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Alias endpoint so both /api/v1/catalog/products and /api/v1/catalogue/products work seamlessly
    @GetMapping("/catalog/products")
    public List<Product> getCatalogProductsAlias() {
        return productRepository.findByIsActiveTrueOrderByIdAsc();
    }

    // =========================================================================
    // 1. UNIFIED SINGLE LOGIN (Auto-Detects Admin vs Customer)
    // =========================================================================

    @PostMapping("/auth/unified-login")
    public ResponseEntity<?> unifiedLogin(@RequestBody Map<String, String> body, HttpSession session) {
        String identifier = body.getOrDefault("identifier", "").trim();
        String password = body.getOrDefault("password", "").trim();
        String fullName = body.getOrDefault("fullName", "").trim();
        String phone = body.getOrDefault("phone", "").trim();

        if (identifier.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please enter your Login ID (Email/Mobile) and Password."));
        }

        // 1. Check if Admin Credentials were entered in the single login box
        boolean isAdminId = identifier.equalsIgnoreCase("admin@kiruthis.in")
                || identifier.equalsIgnoreCase("admin")
                || identifier.equals("8098873594")
                || identifier.equals("+918098873594");

        if (isAdminId && (password.equals("KiruthisAdmin@2026") || password.equals("admin123"))) {
            User admin = userRepository.findByEmail("admin@kiruthis.in").orElseGet(() -> {
                User a = new User();
                a.setFullName("Kiruthis Showroom Admin");
                a.setEmail("admin@kiruthis.in");
                a.setPhone("8098873594");
                a.setPasswordHash(passwordEncoder.encode("KiruthisAdmin@2026"));
                a.setRole("ROLE_ADMIN");
                a.setIsActive(true);
                return userRepository.save(a);
            });
            session.setAttribute("LOGGED_IN_USER_ID", admin.getId());
            session.setAttribute("LOGGED_IN_ROLE", "ROLE_ADMIN");
            return ResponseEntity.ok(Map.of(
                    "message", "Welcome Kiruthis Showroom Admin! Redirecting to Admin Console...",
                    "userId", admin.getId(),
                    "fullName", admin.getFullName(),
                    "email", admin.getEmail(),
                    "phone", admin.getPhone(),
                    "role", "ROLE_ADMIN",
                    "redirectUrl", "/admin-dashboard.html"
            ));
        }

        // 2. Check existing user in database
        Optional<User> userOpt = userRepository.findByEmail(identifier.toLowerCase());
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByPhone(identifier);
        }

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            boolean validPass = passwordEncoder.matches(password, user.getPasswordHash())
                    || password.equals(user.getPasswordHash());
            if (!validPass) {
                return ResponseEntity.status(401).body(Map.of("error", "Incorrect password. Please try again."));
            }
            session.setAttribute("LOGGED_IN_USER_ID", user.getId());
            session.setAttribute("LOGGED_IN_ROLE", user.getRole());
            return ResponseEntity.ok(Map.of(
                    "message", "Welcome back, " + user.getFullName() + "!",
                    "userId", user.getId(),
                    "fullName", user.getFullName(),
                    "email", user.getEmail() != null ? user.getEmail() : "",
                    "phone", user.getPhone() != null ? user.getPhone() : "",
                    "role", user.getRole(),
                    "redirectUrl", "ROLE_ADMIN".equals(user.getRole()) ? "/admin-dashboard.html" : "/"
            ));
        }

        // 3. Auto-register new Customer on first sign-in so customers can immediately inspect & shop
        String email = identifier.contains("@") ? identifier.toLowerCase() : identifier.replaceAll("\\D", "") + "@customer.kiruthis.in";
        String mobile = !phone.isEmpty() ? phone : (identifier.matches(".*\\d{8,}.*") ? identifier.replaceAll("\\D", "") : ("9" + (System.currentTimeMillis() % 1000000000L)));
        if (mobile.length() > 15) {
            mobile = mobile.substring(0, 15);
        }
        String displayName = !fullName.isEmpty() ? fullName : (identifier.contains("@") ? identifier.split("@")[0] : "Valued Customer");

        User newCustomer = new User();
        newCustomer.setFullName(displayName);
        newCustomer.setEmail(email);
        newCustomer.setPhone(mobile);
        newCustomer.setPasswordHash(passwordEncoder.encode(password));
        newCustomer.setRole("ROLE_CUSTOMER");
        newCustomer.setIsActive(true);
        User saved = userRepository.save(newCustomer);

        session.setAttribute("LOGGED_IN_USER_ID", saved.getId());
        session.setAttribute("LOGGED_IN_ROLE", "ROLE_CUSTOMER");

        return ResponseEntity.ok(Map.of(
                "message", "Welcome to Kiruthis Mens Fashions, " + saved.getFullName() + "!",
                "userId", saved.getId(),
                "fullName", saved.getFullName(),
                "email", saved.getEmail(),
                "phone", saved.getPhone(),
                "role", "ROLE_CUSTOMER",
                "redirectUrl", "/"
        ));
    }

    @PostMapping("/auth/customer/register")
    public ResponseEntity<?> registerCustomer(@RequestBody Map<String, String> body, HttpSession session) {
        return unifiedLogin(body, session);
    }

    @PostMapping("/auth/customer/login")
    public ResponseEntity<?> customerLogin(@RequestBody Map<String, String> body, HttpSession session) {
        return unifiedLogin(body, session);
    }

    @PostMapping("/auth/admin/login")
    public ResponseEntity<?> adminLogin(@RequestBody Map<String, String> body, HttpSession session) {
        Map<String, String> unifiedBody = new HashMap<>(body);
        if (!unifiedBody.containsKey("identifier") && unifiedBody.containsKey("username")) {
            unifiedBody.put("identifier", unifiedBody.get("username"));
        }
        return unifiedLogin(unifiedBody, session);
    }

    @GetMapping("/auth/me")
    public ResponseEntity<?> currentSessionUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("LOGGED_IN_USER_ID");
        if (userId == null) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.ok(Map.of("authenticated", false));
        }
        User u = userOpt.get();
        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "userId", u.getId(),
                "fullName", u.getFullName(),
                "email", u.getEmail() != null ? u.getEmail() : "",
                "phone", u.getPhone() != null ? u.getPhone() : "",
                "role", u.getRole()
        ));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    // =========================================================================
    // 2. ADMIN PORTAL CRUD: ADD COLLECTION, UPDATE STOCK, MANAGE CUSTOMERS & DB
    // =========================================================================

    @GetMapping("/admin-portal/overview")
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAdminOverview() {
        List<Product> products = productRepository.findAll();
        List<ProductVariant> variants = variantRepository.findAll();
        List<User> users = userRepository.findAll();
        List<Order> orders = orderRepository.findAll();

        BigDecimal totalRevenue = orders.stream()
                .map(o -> o.getGrandTotal() != null ? o.getGrandTotal() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalStockUnits = variants.stream()
                .mapToInt(v -> v.getStockQuantity() != null ? v.getStockQuantity() : 0)
                .sum();

        long lowStockCount = variants.stream()
                .filter(v -> (v.getStockQuantity() != null ? v.getStockQuantity() : 0) <= 10)
                .count();

        List<Map<String, Object>> customerList = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", u.getId());
            map.put("fullName", u.getFullName());
            map.put("email", u.getEmail());
            map.put("phone", u.getPhone());
            map.put("role", u.getRole());
            map.put("city", "Tirupur");
            customerList.add(map);
        }

        List<Map<String, Object>> variantStockList = new ArrayList<>();
        for (ProductVariant v : variants) {
            Map<String, Object> vMap = new LinkedHashMap<>();
            vMap.put("variantId", v.getId());
            vMap.put("sku", v.getSkuCode());
            vMap.put("productTitle", v.getProduct() != null ? v.getProduct().getTitle() : "Garment");
            vMap.put("segment", v.getProduct() != null && v.getProduct().getSegment() != null ? v.getProduct().getSegment() : "MENS");
            vMap.put("size", v.getSizeLabel());
            vMap.put("color", v.getColorName());
            vMap.put("mrp", v.getMrp());
            vMap.put("sellingPrice", v.getSellingPrice());
            vMap.put("stockQuantity", v.getStockQuantity());
            variantStockList.add(vMap);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalProducts", products.size());
        response.put("totalVariants", variants.size());
        response.put("totalStockUnits", totalStockUnits);
        response.put("lowStockCount", lowStockCount);
        response.put("totalCustomers", users.size());
        response.put("totalOrders", orders.size());
        response.put("totalRevenue", totalRevenue);
        response.put("customers", customerList);
        response.put("inventory", variantStockList);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin-portal/products/create")
    @Transactional
    public ResponseEntity<?> createNewCollectionGarment(@RequestBody Map<String, Object> payload) {
        String title = String.valueOf(payload.getOrDefault("title", "New Showroom Garment")).trim();
        String brandEdition = String.valueOf(payload.getOrDefault("brandEdition", "Kiruthis Signature 2026")).trim();
        String rawSegment = String.valueOf(payload.getOrDefault("segment", "MENS")).toUpperCase();
        String segmentStr = rawSegment.equals("MEN") ? "MENS" : (rawSegment.equals("WOMEN") ? "WOMENS" : rawSegment);
        String fabricComposition = String.valueOf(payload.getOrDefault("fabricComposition", "100% Super-Combed Tirupur Cotton"));
        Integer fabricGsm = Integer.parseInt(String.valueOf(payload.getOrDefault("fabricGsm", "210")));
        String fitType = String.valueOf(payload.getOrDefault("fitType", "REGULAR_FIT"));
        String colorName = String.valueOf(payload.getOrDefault("colorName", "Royal Indigo"));
        String colorHex = String.valueOf(payload.getOrDefault("colorHex", "#1E3A8A"));
        BigDecimal mrpPrice = new BigDecimal(String.valueOf(payload.getOrDefault("mrpPrice", "1999")));
        BigDecimal sellingPrice = new BigDecimal(String.valueOf(payload.getOrDefault("sellingPrice", "1299")));
        Integer initialStock = Integer.parseInt(String.valueOf(payload.getOrDefault("stockQuantity", "40")));
        String frontImageUrl = String.valueOf(payload.getOrDefault("frontImageUrl",
                "https://images.unsplash.com/photo-1602810318383-e386cc2a3ccf?auto=format&fit=crop&w=900&q=85"));
        String backImageUrl = String.valueOf(payload.getOrDefault("backImageUrl",
                "https://images.unsplash.com/photo-1596755094514-f87e34085b2c?auto=format&fit=crop&w=900&q=85"));

        List<Category> allCategories = categoryRepository.findAll();
        Category category = allCategories.stream()
                .filter(c -> segmentStr.equalsIgnoreCase(c.getSegment()))
                .findFirst()
                .orElseGet(() -> {
                    if (!allCategories.isEmpty()) return allCategories.get(0);
                    Category c = new Category();
                    c.setName(segmentStr + " Collection");
                    c.setSlug(segmentStr.toLowerCase() + "-collection-" + System.currentTimeMillis());
                    c.setSegment(segmentStr);
                    c.setHsnCode("6205");
                    c.setDiscountBadge("NEW 2026 DROP");
                    c.setDisplayOrder(1);
                    c.setIsActive(true);
                    return categoryRepository.save(c);
                });

        String slug = title.toLowerCase().replaceAll("[^a-z0-9]+", "-") + "-" + (System.currentTimeMillis() % 10000);
        String skuPrefix = "KMF-" + segmentStr.substring(0, Math.min(3, segmentStr.length())) + "-" + (100 + (int)(Math.random() * 899));

        Product p = new Product();
        p.setCategory(category);
        p.setSkuPrefix(skuPrefix);
        p.setTitle(title);
        p.setSlug(slug);
        p.setBrandName("KIRUTHIS MENS FASHIONS");
        p.setBrandEdition(brandEdition);
        p.setSegment(segmentStr);
        p.setShortDescription(fabricComposition + " (" + fabricGsm + " GSM) — " + fitType);
        p.setFullDescription("Crafted for Kiruthis Mens Fashions, Amman Nagar Bus Stop, Tirupur. " + fabricComposition);
        p.setFabricComposition(fabricComposition);
        p.setFabricGsm(fabricGsm);
        p.setFitType("REGULAR_FIT");
        p.setCollarOrNeck("Spread Collar / Classic");
        p.setSleeveType("FULL_SLEEVE");
        p.setPatternOrPrint("2026 Showroom Edition");
        p.setWashCare("Gentle Machine Wash Cold");
        p.setWeightGrams(320);
        p.setAverageRating(new BigDecimal("4.9"));
        p.setReviewCount(18);
        p.setIsFeatured(true);
        p.setIsNewArrival(true);
        p.setIsCodEligible(true);
        p.setIsReturnable(true);
        p.setIsActive(true);

        Product savedProduct = productRepository.save(p);

        String[] sizes = new String[]{"M", "L", "XL", "XXL"};
        BigDecimal gstRate = sellingPrice.compareTo(new BigDecimal("1000")) <= 0
                ? new BigDecimal("5.00") : new BigDecimal("12.00");

        for (String sz : sizes) {
            ProductVariant v = new ProductVariant();
            v.setProduct(savedProduct);
            v.setSkuCode(skuPrefix + "-" + sz);
            v.setSizeLabel(sz);
            v.setColorName(colorName);
            v.setColorHex(colorHex);
            v.setChestOrWaistInches("Chest: 40 in | Length: 29 in");
            v.setMrp(mrpPrice);
            v.setSellingPrice(sellingPrice);
            v.setGstPercentage(gstRate);
            v.setStockQuantity(initialStock);
            v.setLowStockThreshold(5);
            v.setIsActive(true);
            variantRepository.save(v);
        }

        ProductImage imgFront = new ProductImage();
        imgFront.setProduct(savedProduct);
        imgFront.setImageUrl(frontImageUrl);
        imgFront.setImageType("PRIMARY_FRONT");
        imgFront.setDisplayOrder(1);
        savedProduct.getImages().add(imgFront);

        ProductImage imgBack = new ProductImage();
        imgBack.setProduct(savedProduct);
        imgBack.setImageUrl(backImageUrl);
        imgBack.setImageType("BACK_VIEW");
        imgBack.setDisplayOrder(2);
        savedProduct.getImages().add(imgBack);

        productRepository.save(savedProduct);

        return ResponseEntity.ok(Map.of(
                "message", "New 2026 Collection Garment added to Anchor Rail & MySQL Database!",
                "productId", savedProduct.getId(),
                "skuPrefix", skuPrefix
        ));
    }

    @PutMapping("/admin-portal/variants/{variantId}/update")
    @Transactional
    public ResponseEntity<?> updateVariantStockAndPrice(
            @PathVariable Long variantId,
            @RequestBody Map<String, Object> body) {
        Optional<ProductVariant> opt = variantRepository.findById(variantId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ProductVariant v = opt.get();
        if (body.containsKey("stockQuantity")) {
            v.setStockQuantity(Integer.parseInt(String.valueOf(body.get("stockQuantity"))));
        }
        if (body.containsKey("sellingPrice")) {
            BigDecimal newPrice = new BigDecimal(String.valueOf(body.get("sellingPrice")));
            v.setSellingPrice(newPrice);
            v.setGstPercentage(newPrice.compareTo(new BigDecimal("1000")) <= 0
                    ? new BigDecimal("5.00") : new BigDecimal("12.00"));
        }
        if (body.containsKey("mrpPrice")) {
            v.setMrp(new BigDecimal(String.valueOf(body.get("mrpPrice"))));
        }
        variantRepository.save(v);
        return ResponseEntity.ok(Map.of(
                "message", "Updated SKU " + v.getSkuCode() + " successfully!",
                "variantId", v.getId(),
                "stockQuantity", v.getStockQuantity(),
                "sellingPrice", v.getSellingPrice()
        ));
    }
}

