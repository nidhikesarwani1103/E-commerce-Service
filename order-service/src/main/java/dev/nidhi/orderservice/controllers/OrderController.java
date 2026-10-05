package dev.nidhi.orderservice.controllers;

import dev.nidhi.orderservice.client.PaymentServiceClient;
import dev.nidhi.orderservice.dtos.*;
import dev.nidhi.orderservice.models.Order;
import dev.nidhi.orderservice.repositories.OrderRepository;
import dev.nidhi.orderservice.services.OrderService;
import jakarta.validation.Valid;
import jakarta.ws.rs.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
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

    @GetMapping("/{orderId}/reconcile")
    public ResponseEntity<ReconcileResponseDTO> reconcileOrder(
                         @PathVariable Long orderId){
        ReconcileResponseDTO response = orderService.reconcilePayment(orderId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-for-user/{userId}")
    public List<OrderResponse> getOrderDetails(
            @PathVariable("userId") Long userId){

      List<OrderResponse> orders = orderService.getForUser(userId)
              .stream()
              .map(order -> OrderResponse.fromOrder(order))
              .toList();
        return orders;
    }

}
