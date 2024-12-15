package org.yearup.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.configurations.UserHelper;
import org.yearup.data.OrderDao;
import org.yearup.models.Order;

import java.security.Principal;
import java.sql.SQLException;

@RestController
@RequestMapping("orders")
@PreAuthorize("hasRole('USER') || hasRole('ADMIN')")
@CrossOrigin
public class OrdersController {

    private final OrderDao orderDao;
    private final UserHelper userHelper;

    public OrdersController(OrderDao orderDao, UserHelper userHelper) {
        this.orderDao = orderDao;
        this.userHelper = userHelper;
    }

    @PostMapping
    public ResponseEntity<Order> order(Principal principal) {
        int userId = userHelper.getUserId(principal);

        try {
            Order order = orderDao.create(userId);

            if (order == null) {
                return ResponseEntity.internalServerError().build();
            }

            return ResponseEntity.ok(order);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
