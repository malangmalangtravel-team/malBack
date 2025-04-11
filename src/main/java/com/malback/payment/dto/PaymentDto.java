package com.malback.payment.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private Long id;
    private String impUid;
    private String merchantUid;
    private int amount;
    private String status;
    private String email;
    private String pgProvider;
    private LocalDateTime createdAt;
}
