package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrderItemDao;
import org.yearup.models.OrderItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlOrderItemDao extends MySqlDaoBase implements OrderItemDao {

    public MySqlOrderItemDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public void create(OrderItem orderItem) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO order_line_items (order_id, product_id, sales_price, quantity, discount)
                     VALUES (?, ?, ?, ?, ?)""", PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            statement.setInt(1, orderItem.getOrderId());
            statement.setInt(2, orderItem.getProductId());
            statement.setBigDecimal(3, orderItem.getSalesPrice());
            statement.setInt(4, orderItem.getQuantity());
            statement.setBigDecimal(5, orderItem.getDiscount());

            int rows = statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
