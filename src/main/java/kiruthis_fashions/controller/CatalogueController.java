package kiruthis_fashions.controller;

import kiruthis_fashions.entity.Category;
import kiruthis_fashions.entity.Product;
import kiruthis_fashions.repository.CategoryRepository;
import kiruthis_fashions.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/catalogue")
@CrossOrigin(origins = "*")
public class CatalogueController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public CatalogueController(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Value("${kiruthis.store.name:KIRUTHIS MENS FASHIONS}")
    private String storeName;

    @Value("${kiruthis.store.name-tamil:கிருத்திஸ் மென்ஸ் பேஷன்ஸ்}")
    private String storeNameTamil;

    @Value("${kiruthis.store.tagline:The Trendy Store - Branded Retail Showroom (MENS | KIDS | WOMENS)}")
    private String tagline;

    @Value("${kiruthis.store.gstin:33JODPK0345L1ZW}")
    private String gstin;

    @Value("${kiruthis.store.address:Amman Nagar Bus Stop, Ganapathipalayam Road, Chennimalaipalayam, Tirupur - 641 605}")
    private String address;

    @Value("${kiruthis.store.whatsapp-primary:8098873594}")
    private String whatsappPrimary;

    @Value("${kiruthis.store.phone-secondary:8610426958}")
    private String phoneSecondary;

    @Value("${kiruthis.store.email:kiruthismensfashions23@gmail.com}")
    private String email;

    @Value("${kiruthis.store.instagram:@KIRUTHIS_MENS_FASHIONS}")
    private String instagram;

    @Value("${kiruthis.promo.free-gift-threshold:2000.00}")
    private Double freeGiftThreshold;

    @GetMapping("/store-info")
    public Map<String, Object> getStoreInfo() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("storeName", storeName);
        info.put("storeNameTamil", storeNameTamil);
        info.put("tagline", tagline);
        info.put("gstin", gstin);
        info.put("address", address);
        info.put("whatsappPrimary", whatsappPrimary);
        info.put("phoneSecondary", phoneSecondary);
        info.put("email", email);
        info.put("instagram", instagram);
        info.put("freeGiftThreshold", freeGiftThreshold);
        info.put("categories", categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc());
        info.put("products", productRepository.findByIsActiveTrueOrderByIdAsc());
        return info;
    }

    @GetMapping("/products")
    public List<Product> getProducts(
            @RequestParam(required = false) String segment,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search) {

        List<Product> all = productRepository.findByIsActiveTrueOrderByIdAsc();
        List<Product> filtered = new ArrayList<>();

        for (Product p : all) {
            if (segment != null && !segment.isBlank() && !"ALL".equalsIgnoreCase(segment)) {
                if (!segment.equalsIgnoreCase(p.getSegment())) {
                    continue;
                }
            }
            if (brand != null && !brand.isBlank() && !"ALL".equalsIgnoreCase(brand)) {
                if (!brand.equalsIgnoreCase(p.getBrandName())) {
                    continue;
                }
            }
            if (categoryId != null && p.getCategory() != null) {
                if (!categoryId.equals(p.getCategory().getId())) {
                    continue;
                }
            }
            if (search != null && !search.isBlank()) {
                String q = search.trim().toLowerCase(Locale.ROOT);
                String hay = ((p.getTitle() != null ? p.getTitle() : "") + " "
                        + (p.getBrandName() != null ? p.getBrandName() : "") + " "
                        + (p.getFabricComposition() != null ? p.getFabricComposition() : "") + " "
                        + (p.getPatternOrPrint() != null ? p.getPatternOrPrint() : "") + " "
                        + (p.getSkuPrefix() != null ? p.getSkuPrefix() : "")).toLowerCase(Locale.ROOT);
                if (!hay.contains(q)) {
                    continue;
                }
            }
            filtered.add(p);
        }
        return filtered;
    }

    @GetMapping("/products/{slug}")
    public ResponseEntity<Product> getProductBySlug(@PathVariable String slug) {
        for (Product p : productRepository.findByIsActiveTrueOrderByIdAsc()) {
            if (p.getSlug() != null && p.getSlug().equalsIgnoreCase(slug)) {
                return ResponseEntity.ok(p);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/categories")
    public List<Category> getCategories() {
        return categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc();
    }

    @GetMapping("/pincode-check")
    public Map<String, Object> checkPincodeServiceability(@RequestParam String pincode) {
        Map<String, Object> res = new LinkedHashMap<>();
        String clean = pincode != null ? pincode.trim() : "";
        if (!clean.matches("^[1-8][0-9]{5}$")) {
            res.put("serviceable", false);
            res.put("message", "Please enter a valid 6-digit Indian Pin Code (e.g. 641605).");
            return res;
        }

        res.put("serviceable", true);
        res.put("pincode", clean);
        res.put("codAvailable", true);
        res.put("DispatchOrigin", "Kiruthis Showroom, Ganapathipalayam Rd, Tirupur - 641605");

        if (clean.startsWith("641")) {
            res.put("zone", "Tiruppur / Coimbatore Local Express Zone");
            res.put("estimatedDays", "Same-Day / Next-Day Delivery");
            res.put("courierPartners", "ST Courier / Professional / BlueDart Express");
            res.put("message", "✓ Pin " + clean + " (Tiruppur/Coimbatore Belt): Same-Day / Next-Day Express Delivery & Showroom Pickup Available • Free Delivery & COD Available.");
        } else if (clean.startsWith("60") || clean.startsWith("61") || clean.startsWith("62") || clean.startsWith("63")) {
            res.put("zone", "Tamil Nadu Intra-State Zone (CGST 2.5% + SGST 2.5%)");
            res.put("estimatedDays", "1–2 Working Days");
            res.put("courierPartners", "ST Courier / Delhivery / BlueDart");
            res.put("message", "✓ Pin " + clean + " (Tamil Nadu): 1–2 Days Express Delivery via ST Courier / Delhivery • Both Online UPI & Cash on Delivery Available.");
        } else if (clean.startsWith("56") || clean.startsWith("67") || clean.startsWith("68") || clean.startsWith("69") || clean.startsWith("50")) {
            res.put("zone", "South India Express Zone (IGST)");
            res.put("estimatedDays", "2–3 Working Days");
            res.put("courierPartners", "Delhivery Surface / BlueDart");
            res.put("message", "✓ Pin " + clean + " (South India Zone): 2–3 Working Days via Delhivery / BlueDart • Prepaid UPI & Verified COD Available.");
        } else {
            res.put("zone", "All-India National Courier Zone (IGST)");
            res.put("estimatedDays", "3–5 Working Days");
            res.put("courierPartners", "BlueDart Air / Delhivery / India Post Speed Post");
            res.put("message", "✓ Pin " + clean + " (Pan-India Insured Courier): 3–5 Working Days via Delhivery / BlueDart Air • Prepaid UPI & Verified COD Available.");
        }
        return res;
    }
}