package kiruthis_fashions.dto;

import java.util.ArrayList;
import java.util.List;

public class CheckoutRequest {

    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String shippingAddressLine1;
    private String shippingAddressLine2;
    private String shippingLandmark;
    private String shippingCity;
    private String shippingState;
    private String shippingStateCode = "33";
    private String shippingPincode;

    // 'ONLINE_PREPAID' or 'COD'
    private String paymentMode = "ONLINE_PREPAID";
    private String codOtp;
    private String gatewayPaymentId;

    // Showroom Offer: 'NONE', 'FREE_ROUND_NECK_TSHIRT', 'FREE_BRANDED_SHORTS'
    private String freeGiftChoice = "NONE";
    private String freeGiftSize = "L";

    private List<CartItemDto> items = new ArrayList<>();

    public CheckoutRequest() {
    }

    public static class CartItemDto {
        private Long variantId;
        private Integer quantity = 1;

        public CartItemDto() {
        }

        public Long getVariantId() { return variantId; }
        public void setVariantId(Long variantId) { this.variantId = variantId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

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

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getCodOtp() { return codOtp; }
    public void setCodOtp(String codOtp) { this.codOtp = codOtp; }

    public String getGatewayPaymentId() { return gatewayPaymentId; }
    public void setGatewayPaymentId(String gatewayPaymentId) { this.gatewayPaymentId = gatewayPaymentId; }

    public String getFreeGiftChoice() { return freeGiftChoice; }
    public void setFreeGiftChoice(String freeGiftChoice) { this.freeGiftChoice = freeGiftChoice; }

    public String getFreeGiftSize() { return freeGiftSize; }
    public void setFreeGiftSize(String freeGiftSize) { this.freeGiftSize = freeGiftSize; }

    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; }
}