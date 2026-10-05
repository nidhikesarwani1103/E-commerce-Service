package dev.nidhi.orderservice.repositories;

import dev.nidhi.orderservice.models.Order;
import dev.nidhi.orderservice.models.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    Optional<Order> findById(Long id);
    List<Order> findByStatusIn(List<OrderStatus> statuses);
}
