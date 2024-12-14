package org.yearup.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;

import java.security.Principal;

@RestController
@RequestMapping("orders")
@PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
@CrossOrigin
public class OrdersController {

    private final OrderDao orderDao;

    public OrdersController(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @PostMapping
    public ResponseEntity<Order> order(Principal principal) {
        return null;
    }
}
