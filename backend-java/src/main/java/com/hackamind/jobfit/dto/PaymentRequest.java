package com.hackamind.jobfit.dto;

import jakarta.validation.constraints.NotNull;

public class PaymentRequest {
    @NotNull
    private Long userId;
    private Integer amount = 19900;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Integer getAmount() { return amount; }
    public void setAmount(Integer amount) { this.amount = amount; }
}
