package dev.nidhi.orderservice.services;

import dev.nidhi.orderservice.client.PaymentServiceClient;
import dev.nidhi.orderservice.models.Order;
import dev.nidhi.orderservice.models.OrderStatus;
import dev.nidhi.orderservice.repositories.OrderRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderReconciliationJob {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderReconciliationJob(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @Scheduled(fixedDelay = 5*60*1000)
    public void reconcileOrderPayments(){
        List<Order> orderList = orderRepository.findByStatusIn(
                                        List.of(OrderStatus.CREATED,
                                                OrderStatus.PAYMENT_PENDING));

        for(Order order: orderList){
           orderService.reconcilePayment(order.getId());
        }
    }
}
