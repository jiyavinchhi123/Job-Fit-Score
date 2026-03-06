package com.hackamind.jobfit.repository;

import com.hackamind.jobfit.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
