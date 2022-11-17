package com.bits.ssassignment.orderservice.repository;


import com.bits.ssassignment.orderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    public Order findOrderById(Long id);
}
