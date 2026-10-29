package kiruthis_fashions.service;

import kiruthis_fashions.dto.CheckoutRequest;
import kiruthis_fashions.entity.Category;
import kiruthis_fashions.entity.Order;
import kiruthis_fashions.entity.OrderItem;
import kiruthis_fashions.entity.OrderTrackingEvent;
import kiruthis_fashions.entity.Product;
import kiruthis_fashions.entity.ProductVariant;
import kiruthis_fashions.repository.OrderRepository;
import kiruthis_fashions.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CheckoutService {

    private final ProductVariantRepository variantRepository;
    private final OrderRepository orderRepository;

    // Stores 4-digit COD verification OTPs by 10-digit customer phone
    private final Map<String, String> codOtpStore = new ConcurrentHashMap<>();

    @Value("${kiruthis.promo.free-gift-threshold:2000.00}")
    private BigDecimal freeGiftThreshold;

    @Value("${kiruthis.cod.handling-fee:40.00}")
    private BigDecimal codHandlingFeeConfig;

    @Value("${kiruthis.cod.max-order-amount:8000.00}")
    private BigDecimal codMaxOrderAmount;

    public CheckoutService(ProductVariantRepository variantRepository, OrderRepository orderRepository) {
        this.variantRepository = variantRepository;
        this.orderRepository = orderRepository;
    }

    public String generateCodOtp(String phone) {
        String cleanPhone = phone != null ? phone.replaceAll("[^0-9]", "") : "";
        String otp = String.valueOf(ThreadLocalRandom.current().nextInt(1000, 9999));
        codOtpStore.put(cleanPhone, otp);
        return otp;
    }

    public boolean verifyCodOtp(String phone, String otp) {
        if (otp == null || otp.isBlank()) {
            return false;
        }
        // Accept demo OTP '4829' or the dynamically generated OTP
        if ("4829".equals(otp.trim())) {
            return true;
        }
        String cleanPhone = phone != null ? phone.replaceAll("[^0-9]", "") : "";
        String expected = codOtpStore.get(cleanPhone);
        return expected != null && expected.equals(otp.trim());
    }

    @Transactional
    public Order placeOrder(CheckoutRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("Your shopping bag is empty. Please add at least one garment.");
        }
        if (req.getCustomerName() == null || req.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer full name is required.");
        }
        if (req.getCustomerPhone() == null || !req.getCustomerPhone().trim().matches("^[6-9][0-9]{9}$")) {
            throw new IllegalArgumentException("Please enter a valid 10-digit Indian mobile number for WhatsApp updates.");
        }
        if (req.getShippingPincode() == null || !req.getShippingPincode().trim().matches("^[1-8][0-9]{5}$")) {
            throw new IllegalArgumentException("Please enter a valid 6-digit Indian Pin Code.");
        }

        boolean isCod = "COD".equalsIgnoreCase(req.getPaymentMode());
        if (isCod && !verifyCodOtp(req.getCustomerPhone(), req.getCodOtp())) {
            throw new IllegalArgumentException("Invalid 4-digit COD Mobile OTP. Click 'Send OTP' or enter 4829.");
        }

        // Determine if Intra-State Tamil Nadu (State Code 33 -> CGST + SGST) or Inter-State (IGST)
        String stateCode = resolveStateCode(req.getShippingState(), req.getShippingPincode(), req.getShippingStateCode());
        boolean isIntraStateTamilNadu = "33".equals(stateCode);

        long suffix = System.currentTimeMillis() % 100000;
        String orderNo = "KMF-2026-" + (10000 + suffix % 89999);
        String invoiceNo = "INV-KMF-2026-" + (1000 + suffix % 8999);

        Order order = new Order();
        order.setOrderNumber(orderNo);
        order.setInvoiceNumber(invoiceNo);
        order.setCustomerName(req.getCustomerName().trim());
        order.setCustomerPhone(req.getCustomerPhone().trim());
        order.setCustomerEmail(req.getCustomerEmail() != null ? req.getCustomerEmail().trim() : "");
        order.setShippingAddressLine1(req.getShippingAddressLine1().trim());
        order.setShippingAddressLine2(req.getShippingAddressLine2());
        order.setShippingLandmark(req.getShippingLandmark());
        order.setShippingCity(req.getShippingCity().trim());
        order.setShippingState(req.getShippingState() != null ? req.getShippingState().trim() : "Tamil Nadu");
        order.setShippingStateCode(stateCode);
        order.setShippingPincode(req.getShippingPincode().trim());

        BigDecimal totalTaxable = BigDecimal.ZERO;
        BigDecimal totalCgst = BigDecimal.ZERO;
        BigDecimal totalSgst = BigDecimal.ZERO;
        BigDecimal totalIgst = BigDecimal.ZERO;
        BigDecimal itemsGrossTotal = BigDecimal.ZERO;
        int totalWeightGrams = 50; // 50g tamper-evident courier polybag

        for (CheckoutRequest.CartItemDto itemDto : req.getItems()) {
            int qty = (itemDto.getQuantity() != null && itemDto.getQuantity() > 0) ? itemDto.getQuantity() : 1;

            ProductVariant variant = variantRepository.findById(itemDto.getVariantId())
                    .orElseThrow(() -> new IllegalArgumentException("Product variant ID " + itemDto.getVariantId() + " not found."));

            if (variant.getAvailableStock() < qty) {
                throw new IllegalStateException("Sorry! Only " + variant.getAvailableStock()
                        + " units left for SKU " + variant.getSkuCode() + " (" + variant.getColorName() + " - " + variant.getSizeLabel() + ").");
            }

            // Deduct live stock inside @Transactional boundary
            variant.setStockQuantity(variant.getStockQuantity() - qty);
            variantRepository.save(variant);

            Product product = variant.getProduct();
            Category category = product != null ? product.getCategory() : null;
            String hsnCode = (category != null && category.getHsnCode() != null) ? category.getHsnCode() : "6205";
            String productTitle = product != null ? product.getTitle() : variant.getSkuCode();
            int unitWeight = (product != null && product.getWeightGrams() != null) ? product.getWeightGrams() : 260;
            totalWeightGrams += (unitWeight * qty);

            BigDecimal unitPrice = variant.getSellingPrice();
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(qty)).setScale(2, RoundingMode.HALF_UP);
            BigDecimal gstPct = variant.getGstPercentage() != null ? variant.getGstPercentage() : new BigDecimal("5.00");

            // Inclusive GST calculation: Taxable = LineTotal / (1 + gstPct/100)
            BigDecimal divisor = BigDecimal.ONE.add(gstPct.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
            BigDecimal taxableVal = lineTotal.divide(divisor, 2, RoundingMode.HALF_UP);
            BigDecimal lineTax = lineTotal.subtract(taxableVal);

            BigDecimal lineCgst = BigDecimal.ZERO;
            BigDecimal lineSgst = BigDecimal.ZERO;
            BigDecimal lineIgst = BigDecimal.ZERO;

            if (isIntraStateTamilNadu) {
                lineCgst = lineTax.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP);
                lineSgst = lineTax.subtract(lineCgst);
            } else {
                lineIgst = lineTax;
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(product != null ? product.getId() : 1L);
            orderItem.setVariantId(variant.getId());
            orderItem.setSkuCode(variant.getSkuCode());
            orderItem.setProductTitle(productTitle);
            orderItem.setColorName(variant.getColorName());
            orderItem.setSizeLabel(variant.getSizeLabel());
            orderItem.setHsnCode(hsnCode);
            orderItem.setQuantity(qty);
            orderItem.setUnitSellingPrice(unitPrice);
            orderItem.setGstPercentage(gstPct);
            orderItem.setTaxableValue(taxableVal);
            orderItem.setCgstAmount(lineCgst);
            orderItem.setSgstAmount(lineSgst);
            orderItem.setIgstAmount(lineIgst);
            orderItem.setLineTotal(lineTotal);

            order.getItems().add(orderItem);

            totalTaxable = totalTaxable.add(taxableVal);
            totalCgst = totalCgst.add(lineCgst);
            totalSgst = totalSgst.add(lineSgst);
            totalIgst = totalIgst.add(lineIgst);
            itemsGrossTotal = itemsGrossTotal.add(lineTotal);
        }

        if (isCod && itemsGrossTotal.compareTo(codMaxOrderAmount) > 0) {
            throw new IllegalArgumentException("Cash on Delivery is available for orders up to ₹" + codMaxOrderAmount + ". Please choose Online UPI/Card.");
        }

        BigDecimal codFee = isCod ? codHandlingFeeConfig : BigDecimal.ZERO;
        BigDecimal grandTotal = itemsGrossTotal.add(codFee).setScale(2, RoundingMode.HALF_UP);

        order.setTotalTaxableAmount(totalTaxable);
        order.setCgstAmount(totalCgst);
        order.setSgstAmount(totalSgst);
        order.setIgstAmount(totalIgst);
        order.setShippingFee(BigDecimal.ZERO);
        order.setCodHandlingFee(codFee);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setGrandTotal(grandTotal);
        order.setTotalWeightGrams(totalWeightGrams);

        // Apply Kiruthis Showroom Offer: Orders >= Rs. 2,000 get Free Branded Round Neck T-Shirt or Shorts + Assured Gift
        if (itemsGrossTotal.compareTo(freeGiftThreshold) >= 0) {
            order.setFreeGiftEligible(true);
            String choice = req.getFreeGiftChoice();
            if (!"FREE_ROUND_NECK_TSHIRT".equals(choice) && !"FREE_BRANDED_SHORTS".equals(choice)) {
                choice = "FREE_ROUND_NECK_TSHIRT";
            }
            order.setFreeGiftChoice(choice);
            order.setFreeGiftSize(req.getFreeGiftSize() != null && !req.getFreeGiftSize().isBlank() ? req.getFreeGiftSize() : "L");
        } else {
            order.setFreeGiftEligible(false);
            order.setFreeGiftChoice("NONE");
            order.setFreeGiftSize(null);
        }

        if (isCod) {
            order.setPaymentMode("COD");
            order.setPaymentStatus("COD_OTP_VERIFIED");
            order.setGatewayPaymentId("COD-OTP-VERIFIED");
        } else {
            order.setPaymentMode("ONLINE_PREPAID");
            order.setPaymentStatus("PAID");
            order.setGatewayOrderId("order_Rzp" + suffix);
            order.setGatewayPaymentId(req.getGatewayPaymentId() != null && !req.getGatewayPaymentId().isBlank()
                    ? req.getGatewayPaymentId()
                    : "pay_Rzp" + System.currentTimeMillis() % 10000000);
        }

        order.setOrderStatus("CONFIRMED");
        order.setCourierName("Pending Showroom Dispatch");
        order.setAwbTrackingNumber("Awaiting AWB");

        // Add Initial Showroom Order Tracking Checkpoints
        OrderTrackingEvent ev1 = new OrderTrackingEvent();
        ev1.setOrder(order);
        ev1.setStatusCode("CONFIRMED");
        ev1.setLocationHub("Kiruthis Mens Fashions Showroom, Tirupur - 641605");
        ev1.setRemarks(isCod
                ? "COD Order confirmed via Mobile OTP verification. Stock locked & sent to Tiruppur Showroom Packing Desk."
                : "Prepaid Online Order (" + order.getGatewayPaymentId() + ") confirmed. Stock locked & sent to Tiruppur Showroom Packing Desk.");
        order.getTrackingEvents().add(ev1);

        if (Boolean.TRUE.equals(order.getFreeGiftEligible())) {
            OrderTrackingEvent evGift = new OrderTrackingEvent();
            evGift.setOrder(order);
            evGift.setStatusCode("FREE_GIFT_ADDED");
            evGift.setLocationHub("Kiruthis Showroom Gift Counter, Amman Nagar Bus Stop");
            String giftLabel = "FREE_BRANDED_SHORTS".equals(order.getFreeGiftChoice())
                    ? "Free Branded Casual Shorts"
                    : "Free Branded Round Neck T-Shirt";
            evGift.setRemarks("Showroom Offer (Order ≥ ₹2,000): Added " + giftLabel
                    + " (Size " + order.getFreeGiftSize() + ") + Assured Surprise Gift to parcel!");
            order.getTrackingEvents().add(evGift);
        }

        return orderRepository.save(order);
    }

    private String resolveStateCode(String stateName, String pincode, String fallbackCode) {
        if (pincode != null && pincode.trim().startsWith("6")) {
            String p2 = pincode.trim().substring(0, 2);
            if ("60".equals(p2) || "61".equals(p2) || "62".equals(p2) || "63".equals(p2) || "64".equals(p2)) {
                return "33"; // Tamil Nadu
            }
        }
        if (stateName != null && stateName.trim().equalsIgnoreCase("Tamil Nadu")) {
            return "33";
        }
        if (stateName != null) {
            String s = stateName.trim().toLowerCase();
            if (s.contains("karnataka")) return "29";
            if (s.contains("kerala")) return "32";
            if (s.contains("andhra")) return "37";
            if (s.contains("telangana")) return "36";
            if (s.contains("maharashtra")) return "27";
            if (s.contains("delhi")) return "07";
            if (s.contains("gujarat")) return "24";
            if (s.contains("west bengal")) return "19";
        }
        return (fallbackCode != null && !fallbackCode.isBlank()) ? fallbackCode : "33";
    }
}