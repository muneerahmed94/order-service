package com.bits.ssassignment.orderservice.repository;


import com.bits.ssassignment.orderservice.entity.Order;
import com.bits.ssassignment.orderservice.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findOrderItemsByOrderId(Long orderId);
}
