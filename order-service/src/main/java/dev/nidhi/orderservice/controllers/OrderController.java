package dev.nidhi.orderservice.controllers;

import dev.nidhi.orderservice.client.PaymentServiceClient;
import dev.nidhi.orderservice.dtos.*;
import dev.nidhi.orderservice.models.Order;
import dev.nidhi.orderservice.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/getProduct/{id}")
    public ProductResponse getProductById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token){
        return orderService.getProductById(id, token);
    }

    @PostMapping("/create")
    public OrderCreationResponse createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("Authorization") String token,
            @AuthenticationPrincipal Jwt jwt) {
        return orderService.createOrder(request, token, jwt);
    }


}
