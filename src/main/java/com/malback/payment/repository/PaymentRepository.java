package com.malback.payment.repository;

import com.malback.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByEmailOrderByCreatedAtDesc(String email); // 결제 내역 조회

}
