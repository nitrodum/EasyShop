package org.yearup.data;

import org.yearup.models.Order;

public interface OrderDao {
    Order create(Order order);
}
