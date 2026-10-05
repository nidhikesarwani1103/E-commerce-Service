package dev.nidhi.orderservice.services;

import dev.nidhi.orderservice.client.PaymentServiceClient;
import dev.nidhi.orderservice.client.ProductServiceClient;
import dev.nidhi.orderservice.dtos.*;
import dev.nidhi.orderservice.models.Order;
import dev.nidhi.orderservice.models.OrderItem;
import dev.nidhi.orderservice.models.OrderStatus;
import dev.nidhi.orderservice.repositories.OrderRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {
    private final ProductServiceClient productServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final OrderRepository orderRepository;

    public OrderService(ProductServiceClient productServiceClient,
                        PaymentServiceClient paymentServiceClient,
                        OrderRepository orderRepository) {
        this.productServiceClient = productServiceClient;
        this.paymentServiceClient = paymentServiceClient;
        this.orderRepository = orderRepository;
    }

    public ProductResponse getProductById(Long productId, String token) {
        return productServiceClient.getProductById(productId, token);
    }

    public OrderCreationResponse  createOrder(CreateOrderRequest request,
                                               String token,
                                               Jwt jwt) {
        Order order = new Order();

        Long userId = jwt.getClaim("userId");

        order.setUserId(userId);
        order.setStatus(OrderStatus.PAYMENT_PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;

        for(var itemRequest : request.getItems()) {
            ProductResponse product =
                    productServiceClient.
                            getProductById(itemRequest.getProductId(), token);

            BigDecimal price = BigDecimal.valueOf(product.getPrice());
            BigDecimal totalPrice = price.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity()));

            totalAmount = totalAmount.add(totalPrice);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getTitle());
            orderItem.setPrice(price);
            orderItem.setQuantity(itemRequest.getQuantity());

            order.addItem(orderItem);
        }

        order.setTotalAmount(totalAmount);

        orderRepository.save(order);

        try{
            PaymentResponse paymentResponse = paymentServiceClient
                    .createPayment(
                            new CreatePaymentRequest(
                                    order.getTotalAmount().longValue(),
                                    "INR",
                                    order.getUserId(),
                                    order.getId()
                            ));

            OrderCreationResponse response = new OrderCreationResponse();
            response.setOrderId(order.getId());
            response.setPaymentId(paymentResponse.getId());
            response.setProviderOrderId(paymentResponse.getProviderOrderId());
            response.setAmount(paymentResponse.getAmount());
            response.setCurrency(paymentResponse.getCurrency());
            response.setStatus(order.getStatus());

            order.setPaymentId(paymentResponse.getId());
            orderRepository.save(order);
            return response;
        }
        catch(Exception e){
            order.setStatus(OrderStatus.FAILED);
            orderRepository.save(order);
            throw new RuntimeException("Payment service is not available");
        }

    }

    public ReconcileResponseDTO reconcilePayment(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        Long paymentId = order.getPaymentId();

        ReconcileResponseDTO response = paymentServiceClient
                                              .reconcilePayment(paymentId);

        if("SUCCESS".equals(response.status())){
            order.setStatus(OrderStatus.CONFIRMED);
        }
        else if("FAILED".equals(response.status())){
            order.setStatus(OrderStatus.FAILED);
        }
        orderRepository.save(order);
        return response;
    }

    public List<Order> getForUser(Long userId){
        return orderRepository.findAllByUserId(userId);
    }

}
