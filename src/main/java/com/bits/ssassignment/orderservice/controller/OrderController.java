package com.bits.ssassignment.orderservice.controller;


import com.bits.ssassignment.orderservice.dao.CustomerDao;
import com.bits.ssassignment.orderservice.dao.OrderDao;
import com.bits.ssassignment.orderservice.dao.OrderItemDao;
import com.bits.ssassignment.orderservice.entity.Customer;
import com.bits.ssassignment.orderservice.entity.Order;
import com.bits.ssassignment.orderservice.entity.OrderItem;
import com.bits.ssassignment.orderservice.enums.OrderStatusEnum;
import com.bits.ssassignment.orderservice.model.Notification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;

@RestController("/")
public class OrderController {
    @Autowired
    private OrderDao orderDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @RequestMapping(value="/order", method = RequestMethod.POST)
    @ResponseBody
    public Order placeOrder(@RequestBody Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        Double orderAmount = 0d;

        for (OrderItem orderItem: orderItems) {
            orderAmount += orderItem.getPrice() * orderItem.getQuantity();
        }

        order.setAmount(orderAmount);
        order.setStatus(OrderStatusEnum.PLACED.name());
        order.setDateTime(new Date());
        Order savedOrder = orderDao.save(order);

        orderItems.forEach((orderItem) -> orderItem.setOrderId(order.getId()));
        orderItemDao.createOrderItems(order.getOrderItems());
        savedOrder.setOrderItems(orderItems);

        publishOrderToKafka(order);

        return savedOrder;
    }

    @RequestMapping(value="/order/{orderId}", method = RequestMethod.POST)
    @ResponseBody
    public Order updateOrder(@PathVariable Long orderId, @RequestBody Order order) {
        Order dbOrder = getOrderById(orderId);
        dbOrder.setStatus(order.getStatus());
        orderDao.save(dbOrder);

        Customer customer = customerDao.getCustomerById(dbOrder.getCustomerId());

        notifyCustomer(customer.getEmail(), order);
        return order;
    }

    private void notifyCustomer(String email, Order order) {
        ObjectMapper objectMapper = new ObjectMapper();
        String content;
        try {
            content = objectMapper.writeValueAsString(order);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Notification notification = new Notification();
        notification.setEmail(email);
        notification.setBody(content);
        notification.setSubject("Your order status is updated");

        callNotificationService(notification);
    }

    private void callNotificationService(Notification notification) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "http://localhost:8082/notify";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Notification> request = new HttpEntity<>(notification, headers);

        restTemplate.postForEntity( url, request , Notification.class);
    }

    @RequestMapping(value="/order/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public Order getOrderById(@PathVariable Long orderId) {
        return orderDao.getOrderById(orderId);
    }

    private void publishOrderToKafka(Order order) {
        String url = "http://localhost:8084/kafka";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("topic", "NewOrders");
        rootNode.put("content", convertObjectToString(order));

        HttpEntity<String> request = new HttpEntity<>(rootNode.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity( url, request , String.class );
        System.out.println(response);
    }

    public String convertObjectToString(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            //Converting the Object to JSONString
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value="/order", method = RequestMethod.PATCH)
    @ResponseBody
    public Order updateOrder(@RequestBody Order order) {
        return orderDao.save(order);
    }
}
