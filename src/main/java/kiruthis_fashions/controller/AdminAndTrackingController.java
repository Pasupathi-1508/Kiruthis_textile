package kiruthis_fashions.controller;

import kiruthis_fashions.entity.Order;
import kiruthis_fashions.entity.OrderTrackingEvent;
import kiruthis_fashions.entity.ProductVariant;
import kiruthis_fashions.entity.ReturnRequest;
import kiruthis_fashions.repository.OrderRepository;
import kiruthis_fashions.repository.ProductRepository;
import kiruthis_fashions.repository.ProductVariantRepository;
import kiruthis_fashions.repository.ReturnRequestRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@CrossOrigin(origins = "*")
public class AdminAndTrackingController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final ReturnRequestRepository returnRequestRepository;

    public AdminAndTrackingController(
            OrderRepository orderRepository,
            ProductRepository productRepository,
            ProductVariantRepository variantRepository,
            ReturnRequestRepository returnRequestRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.returnRequestRepository = returnRequestRepository;
    }

    // 1. Get Full Admin Dashboard Summary (KPIs + All Orders + All Products + All Returns)
    @GetMapping("/dashboard")
    public Map<String, Object> getDashboardData() {
        List<Order> allOrders = orderRepository.findAllByOrderByCreatedAtDesc();
        List<ProductVariant> allVariants = variantRepository.findAll();
        List<ReturnRequest> allReturns = returnRequestRepository.findAllByOrderByCreatedAtDesc();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int pendingPackCount = 0;
        int freeGiftOrdersCount = 0;
        int codOrdersCount = 0;

        for (Order o : allOrders) {
            if (!"CANCELLED".equalsIgnoreCase(o.getOrderStatus())
                    && !"RETURNED_AND_REFUNDED".equalsIgnoreCase(o.getOrderStatus())) {
                if (o.getGrandTotal() != null) {
                    totalRevenue = totalRevenue.add(o.getGrandTotal());
                }
            }
            if ("CONFIRMED".equalsIgnoreCase(o.getOrderStatus())
                    || "PACKED".equalsIgnoreCase(o.getOrderStatus())) {
                pendingPackCount++;
            }
            if (Boolean.TRUE.equals(o.getFreeGiftEligible())) {
                freeGiftOrdersCount++;
            }
            if ("COD".equalsIgnoreCase(o.getPaymentMode())) {
                codOrdersCount++;
            }
        }

        int lowStockCount = 0;
        for (ProductVariant v : allVariants) {
            if (v.getAvailableStock() <= 5) {
                lowStockCount++;
            }
        }

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("totalOrders", allOrders.size());
        res.put("totalRevenue", totalRevenue);
        res.put("pendingPackCount", pendingPackCount);
        res.put("freeGiftOrdersCount", freeGiftOrdersCount);
        res.put("codOrdersCount", codOrdersCount);
        res.put("prepaidOrdersCount", allOrders.size() - codOrdersCount);
        res.put("lowStockCount", lowStockCount);
        res.put("orders", allOrders);
        res.put("products", productRepository.findByIsActiveTrueOrderByIdAsc());
        res.put("returns", allReturns);
        return res;
    }

    // 2. Advance Order Status & Assign Courier AWB Number (CONFIRMED -> PACKED -> SHIPPED -> OUT_FOR_DELIVERY -> DELIVERED)
    @PostMapping("/orders/{orderNumber}/status")
    @Transactional
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable String orderNumber,
            @RequestBody Map<String, String> body) {

        Order order = orderRepository.findByOrderNumber(orderNumber.trim()).orElse(null);
        if (order == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("message", "Order not found: " + orderNumber);
            return ResponseEntity.status(404).body(err);
        }

        String nextStatus = body.getOrDefault("status", "PACKED").trim().toUpperCase();
        String courierName = body.get("courierName");
        String awbNumber = body.get("awbNumber");

        order.setOrderStatus(nextStatus);

        OrderTrackingEvent ev = new OrderTrackingEvent();
        ev.setOrder(order);
        ev.setStatusCode(nextStatus);

        if ("PACKED".equals(nextStatus)) {
            ev.setLocationHub("Kiruthis Showroom Packing Desk, Tirupur - 641605");
            ev.setRemarks("Garments quality-checked, folded in moisture-proof bag, and GST Tax Invoice #"
                    + order.getInvoiceNumber() + " placed inside parcel.");
        } else if ("SHIPPED".equals(nextStatus)) {
            if (courierName == null || courierName.isBlank()) {
                courierName = "33".equals(order.getShippingStateCode())
                        ? "ST Courier / BlueDart Priority"
                        : "Delhivery All-India Express";
            }
            if (awbNumber == null || awbNumber.isBlank()) {
                String prefix = "33".equals(order.getShippingStateCode()) ? "STC" : "DLV";
                awbNumber = prefix + (100000000L + (System.currentTimeMillis() % 899999999L)) + "IN";
            }
            order.setCourierName(courierName);
            order.setAwbTrackingNumber(awbNumber);
            ev.setLocationHub("Tirupur Courier Dispatch Hub (641605)");
            ev.setRemarks("Handed over to " + courierName + " (AWB: " + awbNumber
                    + "). Live tracking active to " + order.getShippingCity() + " - " + order.getShippingPincode() + ".");
        } else if ("OUT_FOR_DELIVERY".equals(nextStatus)) {
            ev.setLocationHub(order.getShippingCity() + " Local Delivery Hub (" + order.getShippingPincode() + ")");
            ev.setRemarks("Parcel is Out for Delivery with courier executive in " + order.getShippingCity() + ".");
        } else if ("DELIVERED".equals(nextStatus)) {
            order.setDeliveredAt(LocalDateTime.now());
            if ("COD".equalsIgnoreCase(order.getPaymentMode())) {
                order.setPaymentStatus("PAID");
            }
            ev.setLocationHub(order.getShippingCity() + " - " + order.getShippingPincode());
            ev.setRemarks("Delivered to " + order.getCustomerName()
                    + ". 7-Day Doorstep Size Exchange window is now active.");
        } else {
            ev.setLocationHub("Kiruthis Mens Fashions, Tirupur");
            ev.setRemarks("Order status updated to " + nextStatus);
        }

        order.getTrackingEvents().add(ev);
        Order saved = orderRepository.save(order);
        return ResponseEntity.ok(saved);
    }

    // 3. Quick Live Stock Adjustment (+5 / -1) from Admin Inventory Table
    @PostMapping("/variants/{variantId}/adjust-stock")
    @Transactional
    public ResponseEntity<?> adjustVariantStock(
            @PathVariable Long variantId,
            @RequestBody Map<String, Integer> body) {

        ProductVariant variant = variantRepository.findById(variantId).orElse(null);
        if (variant == null) {
            return ResponseEntity.notFound().build();
        }
        int delta = body.getOrDefault("delta", 1);
        int updated = Math.max(0, variant.getStockQuantity() + delta);
        variant.setStockQuantity(updated);
        variantRepository.save(variant);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("variantId", variant.getId());
        res.put("skuCode", variant.getSkuCode());
        res.put("stockQuantity", variant.getStockQuantity());
        return ResponseEntity.ok(res);
    }

    // 4. Customer 7-Day Return / Size Exchange Request API
    @PostMapping("/returns/request")
    @Transactional
    public ResponseEntity<?> submitReturnRequest(@RequestBody Map<String, String> body) {
        String orderNumber = body.get("orderNumber");
        String requestType = body.getOrDefault("requestType", "SIZE_EXCHANGE");
        String reason = body.getOrDefault("reason", "Size exchange requested by customer");
        String comments = body.getOrDefault("comments", "");
        String refundUpiId = body.getOrDefault("refundUpiId", "");

        Order order = orderRepository.findByOrderNumber(orderNumber != null ? orderNumber.trim() : "").orElse(null);
        if (order == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("message", "Order not found: " + orderNumber);
            return ResponseEntity.status(404).body(err);
        }

        Long firstOrderItemId = order.getItems().isEmpty() ? 1L : order.getItems().get(0).getId();

        ReturnRequest rr = new ReturnRequest();
        rr.setOrderId(order.getId());
        rr.setOrderItemId(firstOrderItemId);
        rr.setRequestType(requestType);
        rr.setReason(reason);
        rr.setCustomerComments(comments);
        rr.setRefundUpiId(refundUpiId);
        rr.setReturnStatus("APPROVED_RVP_SCHEDULED");
        rr.setReverseAwbNumber("RVP-" + (100000 + System.currentTimeMillis() % 899999));
        returnRequestRepository.save(rr);

        order.setOrderStatus("RETURN_REQUESTED");
        OrderTrackingEvent ev = new OrderTrackingEvent();
        ev.setOrder(order);
        ev.setStatusCode("RETURN_REQUESTED");
        ev.setLocationHub(order.getShippingCity() + " Doorstep Reverse Pickup");
        ev.setRemarks("7-Day " + requestType.replace("_", " ") + " scheduled (" + reason
                + "). Reverse Courier AWB: " + rr.getReverseAwbNumber());
        order.getTrackingEvents().add(ev);
        orderRepository.save(order);

        return ResponseEntity.ok(rr);
    }
}