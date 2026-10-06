package dev.nidhi.orderservice.dtos;

import dev.nidhi.orderservice.models.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemResponse {
    private Long id;
    private String productName;
    private Integer quantity;

    public static OrderItemResponse fromOrderItem(OrderItem item){
        OrderItemResponse itemResponse = new OrderItemResponse();
        itemResponse.setId(item.getId());
        itemResponse.setProductName(item.getProductName());
        itemResponse.setQuantity(item.getQuantity());
        return itemResponse;
    }
}

