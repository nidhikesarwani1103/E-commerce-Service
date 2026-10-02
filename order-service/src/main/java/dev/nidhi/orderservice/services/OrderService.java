package dev.nidhi.orderservice.services;

import dev.nidhi.orderservice.client.PaymentServiceClient;
import dev.nidhi.orderservice.client.ProductServiceClient;
import dev.nidhi.orderservice.dtos.CreateOrderRequest;
import dev.nidhi.orderservice.dtos.CreatePaymentRequest;
import dev.nidhi.orderservice.dtos.PaymentResponse;
import dev.nidhi.orderservice.dtos.ProductResponse;
import dev.nidhi.orderservice.models.Order;
import dev.nidhi.orderservice.models.OrderItem;
import dev.nidhi.orderservice.models.OrderStatus;
import dev.nidhi.orderservice.repositories.OrderRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    public Order createOrder(CreateOrderRequest request,
                             String token, Jwt jwt) {
        Order order = new Order();

        Long userId = jwt.getClaim("userId");

        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);

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
        return order;
    }

    public PaymentResponse createPayment(CreatePaymentRequest request){
        return paymentServiceClient.createPayment(request);
    }
}
