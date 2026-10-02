package dev.nidhi.orderservice.dtos;

import dev.nidhi.orderservice.models.OrderStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderCreationResponse {
    private Long orderId;
    private Long paymentId;
    private String providerOrderId;
    private Long amount;
    private String currency;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
