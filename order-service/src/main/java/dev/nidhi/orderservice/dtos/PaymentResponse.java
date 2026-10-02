package dev.nidhi.orderservice.dtos;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {
    private Long id;
    private Long amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private Long userId;
    private Long orderId;

    private String providerOrderId;
    private String providerPaymentId;
}

