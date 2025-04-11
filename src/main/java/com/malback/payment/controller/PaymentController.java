package com.malback.payment.controller;

import com.malback.payment.dto.PaymentDto;
import com.malback.payment.entity.Payment;
import com.malback.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/save")
    public ResponseEntity<Void> savePayment(@RequestBody PaymentDto paymentDto) {
        paymentService.save(paymentDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<Payment>> getPaymentHistory(@RequestParam String email) {
        List<Payment> payments = paymentService.getPaymentHistoryByEmail(email);
        return ResponseEntity.ok(payments);
    }
}
