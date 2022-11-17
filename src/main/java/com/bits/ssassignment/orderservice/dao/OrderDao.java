package com.bits.ssassignment.orderservice.dao;

import com.bits.ssassignment.orderservice.entity.Order;
import com.bits.ssassignment.orderservice.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderDao {
    @Autowired
    private OrderRepository orderRepository;

    public  Order save(Order order) {
        return orderRepository.save(order);
    }

    public  Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).get();
    }
}
