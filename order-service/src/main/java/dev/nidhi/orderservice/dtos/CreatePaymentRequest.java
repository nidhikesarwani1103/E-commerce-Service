package dev.nidhi.orderservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreatePaymentRequest {
    private Long amount;
    private String currency;
    private Long userId;
    private Long orderId;
}
