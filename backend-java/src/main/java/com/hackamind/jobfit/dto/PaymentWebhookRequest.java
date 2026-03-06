package com.hackamind.jobfit.dto;

public class PaymentWebhookRequest {
    private Long userId;
    private String razorpayPaymentId;
    private String status;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
