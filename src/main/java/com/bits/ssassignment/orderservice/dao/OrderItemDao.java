package com.bits.ssassignment.orderservice.dao;

import com.bits.ssassignment.orderservice.entity.Order;
import com.bits.ssassignment.orderservice.entity.OrderItem;
import com.bits.ssassignment.orderservice.repository.OrderItemRepository;
import com.bits.ssassignment.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemDao {
    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findOrderItemsByOrderId(orderId);
    }

    public List<OrderItem> createOrderItems(List<OrderItem> orderItems) {
        return orderItemRepository.saveAll(orderItems);
    }
}
