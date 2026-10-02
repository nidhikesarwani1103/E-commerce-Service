package dev.nidhi.orderservice.controllers;

import dev.nidhi.orderservice.client.PaymentServiceClient;
import dev.nidhi.orderservice.dtos.CreateOrderRequest;
import dev.nidhi.orderservice.dtos.CreatePaymentRequest;
import dev.nidhi.orderservice.dtos.PaymentResponse;
import dev.nidhi.orderservice.dtos.ProductResponse;
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
    public Order createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader("Authorization") String token,
            @AuthenticationPrincipal Jwt jwt) {
        return orderService.createOrder(request, token, jwt);
    }

    @PostMapping("/make-payment")
    public PaymentResponse makePayment(
            @RequestBody CreatePaymentRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        request.setUserId(userId);
        return orderService.createPayment(request);
    }

}
