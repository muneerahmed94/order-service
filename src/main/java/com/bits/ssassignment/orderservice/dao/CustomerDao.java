package com.bits.ssassignment.orderservice.dao;

import com.bits.ssassignment.orderservice.entity.Customer;
import com.bits.ssassignment.orderservice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerDao {
    @Autowired
    private CustomerRepository customerRepository;

    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId).get();
    }
}
