package com.hackamind.jobfit.controller;

import com.hackamind.jobfit.dto.PaymentRequest;
import com.hackamind.jobfit.dto.PaymentWebhookRequest;
import com.hackamind.jobfit.service.PaymentService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-payment")
    public Map<String, Object> createPayment(@Valid @RequestBody PaymentRequest request) {
        return paymentService.createPayment(request);
    }

    @PostMapping("/payment-webhook")
    public Map<String, Object> paymentWebhook(@RequestBody PaymentWebhookRequest request) {
        return paymentService.handleWebhook(request);
    }
}
