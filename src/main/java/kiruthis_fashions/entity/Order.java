package kiruthis_fashions.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 40)
    private String orderNumber; // e.g. 'KMF-2026-10001'

    @Column(name = "invoice_number", unique = true, length = 40)
    private String invoiceNumber; // e.g. 'INV-KMF-2026-0001'

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "customer_name", nullable = false, length = 120)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 15)
    private String customerPhone;

    @Column(name = "customer_email", length = 150)
    private String customerEmail;

    @Column(name = "shipping_address_line1", nullable = false, length = 255)
    private String shippingAddressLine1;

    @Column(name = "shipping_address_line2", length = 255)
    private String shippingAddressLine2;

    @Column(name = "shipping_landmark", length = 150)
    private String shippingLandmark;

    @Column(name = "shipping_city", nullable = false, length = 100)
    private String shippingCity;

    @Column(name = "shipping_state", nullable = false, length = 100)
    private String shippingState;

    @Column(name = "shipping_state_code", nullable = false, length = 5)
    private String shippingStateCode = "33";

    @Column(name = "shipping_pincode", nullable = false, length = 6)
    private String shippingPincode;

    @Column(name = "total_taxable_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalTaxableAmount = BigDecimal.ZERO;

    @Column(name = "cgst_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal cgstAmount = BigDecimal.ZERO;

    @Column(name = "sgst_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal sgstAmount = BigDecimal.ZERO;

    @Column(name = "igst_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal igstAmount = BigDecimal.ZERO;

    @Column(name = "shipping_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingFee = BigDecimal.ZERO;

    @Column(name = "cod_handling_fee", nullable = false, precision = 10, scale = 2)
    private BigDecimal codHandlingFee = BigDecimal.ZERO;

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "grand_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    // Kiruthis Showroom Offer: Orders >= Rs. 2,000 get Free Branded Round Neck T-Shirt or Shorts + Assured Gift
    @Column(name = "free_gift_eligible", nullable = false)
    private Boolean freeGiftEligible = false;

    @Column(name = "free_gift_choice", nullable = false, length = 40)
    private String freeGiftChoice = "NONE"; // 'NONE', 'FREE_ROUND_NECK_TSHIRT', 'FREE_BRANDED_SHORTS', 'ASSURED_SURPRISE_GIFT'

    @Column(name = "free_gift_size", length = 20)
    private String freeGiftSize;

    @Column(name = "payment_mode", nullable = false, length = 25)
    private String paymentMode; // 'ONLINE_PREPAID', 'COD'

    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus = "PENDING"; // 'PENDING', 'PAID', 'COD_OTP_VERIFIED', 'FAILED', 'REFUNDED'

    @Column(name = "gateway_order_id", length = 100)
    private String gatewayOrderId;

    @Column(name = "gateway_payment_id", length = 100)
    private String gatewayPaymentId;

    @Column(name = "gateway_signature", length = 255)
    private String gatewaySignature;

    @Column(name = "order_status", nullable = false, length = 40)
    private String orderStatus = "CREATED";

    @Column(name = "total_weight_grams", nullable = false)
    private Integer totalWeightGrams = 350;

    @Column(name = "courier_name", length = 80)
    private String courierName;

    @Column(name = "awb_tracking_number", length = 80)
    private String awbTrackingNumber;

    @Column(name = "shipping_label_url", length = 500)
    private String shippingLabelUrl;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<OrderTrackingEvent> trackingEvents = new ArrayList<>();

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Order() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getShippingAddressLine1() { return shippingAddressLine1; }
    public void setShippingAddressLine1(String shippingAddressLine1) { this.shippingAddressLine1 = shippingAddressLine1; }

    public String getShippingAddressLine2() { return shippingAddressLine2; }
    public void setShippingAddressLine2(String shippingAddressLine2) { this.shippingAddressLine2 = shippingAddressLine2; }

    public String getShippingLandmark() { return shippingLandmark; }
    public void setShippingLandmark(String shippingLandmark) { this.shippingLandmark = shippingLandmark; }

    public String getShippingCity() { return shippingCity; }
    public void setShippingCity(String shippingCity) { this.shippingCity = shippingCity; }

    public String getShippingState() { return shippingState; }
    public void setShippingState(String shippingState) { this.shippingState = shippingState; }

    public String getShippingStateCode() { return shippingStateCode; }
    public void setShippingStateCode(String shippingStateCode) { this.shippingStateCode = shippingStateCode; }

    public String getShippingPincode() { return shippingPincode; }
    public void setShippingPincode(String shippingPincode) { this.shippingPincode = shippingPincode; }

    public BigDecimal getTotalTaxableAmount() { return totalTaxableAmount; }
    public void setTotalTaxableAmount(BigDecimal totalTaxableAmount) { this.totalTaxableAmount = totalTaxableAmount; }

    public BigDecimal getCgstAmount() { return cgstAmount; }
    public void setCgstAmount(BigDecimal cgstAmount) { this.cgstAmount = cgstAmount; }

    public BigDecimal getSgstAmount() { return sgstAmount; }
    public void setSgstAmount(BigDecimal sgstAmount) { this.sgstAmount = sgstAmount; }

    public BigDecimal getIgstAmount() { return igstAmount; }
    public void setIgstAmount(BigDecimal igstAmount) { this.igstAmount = igstAmount; }

    public BigDecimal getShippingFee() { return shippingFee; }
    public void setShippingFee(BigDecimal shippingFee) { this.shippingFee = shippingFee; }

    public BigDecimal getCodHandlingFee() { return codHandlingFee; }
    public void setCodHandlingFee(BigDecimal codHandlingFee) { this.codHandlingFee = codHandlingFee; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getGrandTotal() { return grandTotal; }
    public void setGrandTotal(BigDecimal grandTotal) { this.grandTotal = grandTotal; }

    public Boolean getFreeGiftEligible() { return freeGiftEligible; }
    public void setFreeGiftEligible(Boolean freeGiftEligible) { this.freeGiftEligible = freeGiftEligible; }

    public String getFreeGiftChoice() { return freeGiftChoice; }
    public void setFreeGiftChoice(String freeGiftChoice) { this.freeGiftChoice = freeGiftChoice; }

    public String getFreeGiftSize() { return freeGiftSize; }
    public void setFreeGiftSize(String freeGiftSize) { this.freeGiftSize = freeGiftSize; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getGatewayOrderId() { return gatewayOrderId; }
    public void setGatewayOrderId(String gatewayOrderId) { this.gatewayOrderId = gatewayOrderId; }

    public String getGatewayPaymentId() { return gatewayPaymentId; }
    public void setGatewayPaymentId(String gatewayPaymentId) { this.gatewayPaymentId = gatewayPaymentId; }

    public String getGatewaySignature() { return gatewaySignature; }
    public void setGatewaySignature(String gatewaySignature) { this.gatewaySignature = gatewaySignature; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public Integer getTotalWeightGrams() { return totalWeightGrams; }
    public void setTotalWeightGrams(Integer totalWeightGrams) { this.totalWeightGrams = totalWeightGrams; }

    public String getCourierName() { return courierName; }
    public void setCourierName(String courierName) { this.courierName = courierName; }

    public String getAwbTrackingNumber() { return awbTrackingNumber; }
    public void setAwbTrackingNumber(String awbTrackingNumber) { this.awbTrackingNumber = awbTrackingNumber; }

    public String getShippingLabelUrl() { return shippingLabelUrl; }
    public void setShippingLabelUrl(String shippingLabelUrl) { this.shippingLabelUrl = shippingLabelUrl; }

    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public List<OrderTrackingEvent> getTrackingEvents() { return trackingEvents; }
    public void setTrackingEvents(List<OrderTrackingEvent> trackingEvents) { this.trackingEvents = trackingEvents; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
