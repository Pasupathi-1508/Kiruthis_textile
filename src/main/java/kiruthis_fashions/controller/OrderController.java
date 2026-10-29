package kiruthis_fashions.controller;

import kiruthis_fashions.dto.CheckoutRequest;
import kiruthis_fashions.entity.Order;
import kiruthis_fashions.repository.OrderRepository;
import kiruthis_fashions.service.CheckoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;

    public OrderController(CheckoutService checkoutService, OrderRepository orderRepository) {
        this.checkoutService = checkoutService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/send-cod-otp")
    public ResponseEntity<Map<String, Object>> sendCodOtp(@RequestBody Map<String, String> body) {
        String phone = body.get("phone");
        Map<String, Object> res = new LinkedHashMap<>();
        if (phone == null || !phone.trim().matches("^[6-9][0-9]{9}$")) {
            res.put("success", false);
            res.put("message", "Please enter a valid 10-digit mobile number first.");
            return ResponseEntity.badRequest().body(res);
        }
        String otp = checkoutService.generateCodOtp(phone.trim());
        res.put("success", true);
        res.put("phone", phone.trim());
        res.put("demoOtp", otp);
        res.put("message", "✓ 4-Digit COD Verification OTP sent to +91 " + phone.trim() + " (OTP: " + otp + ")");
        return ResponseEntity.ok(res);
    }

    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody CheckoutRequest request) {
        try {
            Order savedOrder = checkoutService.placeOrder(request);
            return ResponseEntity.ok(savedOrder);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", ex.getMessage());
            return ResponseEntity.badRequest().body(err);
        } catch (Exception ex) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "Could not place order: " + ex.getMessage());
            return ResponseEntity.internalServerError().body(err);
        }
    }

    @GetMapping("/{orderNumber}")
    public ResponseEntity<?> getOrderByNumber(@PathVariable String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber.trim())
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> {
                    Map<String, Object> err = new LinkedHashMap<>();
                    err.put("message", "Order not found: " + orderNumber);
                    return ResponseEntity.status(404).body(err);
                });
    }

    @GetMapping("/by-phone")
    public List<Order> getOrdersByCustomerPhone(@RequestParam String phone) {
        return orderRepository.findByCustomerPhoneOrderByCreatedAtDesc(phone.trim());
    }

    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }
}
