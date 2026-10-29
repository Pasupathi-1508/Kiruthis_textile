package kiruthis_fashions.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "return_requests")
public class ReturnRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "request_type", nullable = false, length = 30)
    private String requestType = "SIZE_EXCHANGE"; // 'SIZE_EXCHANGE', 'REFUND'

    @Column(name = "exchange_new_variant_id")
    private Long exchangeNewVariantId;

    @Column(nullable = false, length = 255)
    private String reason;

    @Column(name = "customer_comments", length = 500)
    private String customerComments;

    @Column(name = "unboxing_photo_url", length = 500)
    private String unboxingPhotoUrl;

    @Column(name = "refund_upi_id", length = 100)
    private String refundUpiId;

    @Column(name = "return_status", nullable = false, length = 40)
    private String returnStatus = "PENDING_APPROVAL";

    @Column(name = "reverse_awb_number", length = 80)
    private String reverseAwbNumber;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public ReturnRequest() {
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public Long getOrderItemId() { return orderItemId; }
    public void setOrderItemId(Long orderItemId) { this.orderItemId = orderItemId; }

    public String getRequestType() { return requestType; }
    public void setRequestType(String requestType) { this.requestType = requestType; }

    public Long getExchangeNewVariantId() { return exchangeNewVariantId; }
    public void setExchangeNewVariantId(Long exchangeNewVariantId) { this.exchangeNewVariantId = exchangeNewVariantId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getCustomerComments() { return customerComments; }
    public void setCustomerComments(String customerComments) { this.customerComments = customerComments; }

    public String getUnboxingPhotoUrl() { return unboxingPhotoUrl; }
    public void setUnboxingPhotoUrl(String unboxingPhotoUrl) { this.unboxingPhotoUrl = unboxingPhotoUrl; }

    public String getRefundUpiId() { return refundUpiId; }
    public void setRefundUpiId(String refundUpiId) { this.refundUpiId = refundUpiId; }

    public String getReturnStatus() { return returnStatus; }
    public void setReturnStatus(String returnStatus) { this.returnStatus = returnStatus; }

    public String getReverseAwbNumber() { return reverseAwbNumber; }
    public void setReverseAwbNumber(String reverseAwbNumber) { this.reverseAwbNumber = reverseAwbNumber; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
