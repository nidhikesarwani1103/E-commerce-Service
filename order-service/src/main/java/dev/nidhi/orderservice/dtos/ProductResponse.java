package dev.nidhi.orderservice.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductResponse {
    private Long id;
    private String title;
    private Double price;
}
