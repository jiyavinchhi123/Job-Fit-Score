package com.hackamind.jobfit.service;

import com.hackamind.jobfit.dto.PaymentRequest;
import com.hackamind.jobfit.dto.PaymentWebhookRequest;
import com.hackamind.jobfit.model.Payment;
import com.hackamind.jobfit.model.User;
import com.hackamind.jobfit.repository.PaymentRepository;
import com.hackamind.jobfit.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKeyId;

    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> createPayment(PaymentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Map<String, Object> response = new HashMap<>();
        response.put("key", razorpayKeyId);
        response.put("amount", request.getAmount());
        response.put("currency", "INR");
        response.put("name", "Resume -> Job Fit Score");
        response.put("description", "Pro Plan Upgrade");
        response.put("orderId", "order_" + UUID.randomUUID().toString().replace("-", ""));
        response.put("userId", user.getId());
        return response;
    }

    @Transactional
    public Map<String, Object> handleWebhook(PaymentWebhookRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Payment payment = new Payment();
        payment.setUser(user);
        payment.setRazorpayPaymentId(request.getRazorpayPaymentId());
        payment.setStatus(request.getStatus());
        paymentRepository.save(payment);

        if ("success".equalsIgnoreCase(request.getStatus())) {
            user.setPlan("pro");
            userRepository.save(user);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Webhook processed");
        response.put("plan", user.getPlan());
        return response;
    }
}
