package dev.nidhi.orderservice.dtos;

import dev.nidhi.orderservice.models.Order;
import dev.nidhi.orderservice.models.OrderItem;
import dev.nidhi.orderservice.models.OrderStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Long id;
    private BigDecimal amount;
    private List<OrderItemResponse> items = new ArrayList<>();
    private OrderStatus status;
    private Long paymentId;

    public static OrderResponse fromOrder(Order order){

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(item ->
                        OrderItemResponse.fromOrderItem(item))
                .toList();

        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setId(order.getId());
        orderResponse.setStatus(order.getStatus());
        orderResponse.setPaymentId(order.getPaymentId());
        orderResponse.setItems(items);
        orderResponse.setAmount(order.getTotalAmount());

        return orderResponse;
    }
}
