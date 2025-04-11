package com.malback.payment.service;

import com.malback.payment.repository.PaymentRepository;
import com.malback.payment.dto.PaymentDto;
import com.malback.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // 결제 시 결제 정보 저장
    public void save(PaymentDto dto) {
        System.out.println("===== 결제 정보 저장 시작 =====");
        System.out.println("dto: " + dto); // DTO 전체 로그
        System.out.println("impUid: " + dto.getImpUid());
        System.out.println("pgProvider: " + dto.getPgProvider()); // 👈 이게 null이라면 프론트 문제일 가능성 ↑
        System.out.println("===== 결제 정보 저장 종료 =====");

        Payment payment = Payment.builder()
                .impUid(dto.getImpUid())
                .merchantUid(dto.getMerchantUid())
                .amount(dto.getAmount())
                .status(dto.getStatus())
                .email(dto.getEmail())
                .pgProvider(dto.getPgProvider())
                .build();

        paymentRepository.save(payment);
    }

    // 결제 내역
    public List<Payment> getPaymentHistoryByEmail(String email) {
        return paymentRepository.findByEmailOrderByCreatedAtDesc(email);
    }
}
