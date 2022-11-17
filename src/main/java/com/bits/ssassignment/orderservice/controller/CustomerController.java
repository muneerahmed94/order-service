package com.bits.ssassignment.orderservice.controller;

import com.bits.ssassignment.orderservice.dao.CustomerDao;
import com.bits.ssassignment.orderservice.entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class CustomerController {
    @Autowired
    private CustomerDao customerDao;

    @RequestMapping(value="/customer/{customerId}", method = RequestMethod.GET)
    @ResponseBody
    public Customer getCustomerById(@PathVariable Long customerId) {
        return customerDao.getCustomerById(customerId);
    }
}
